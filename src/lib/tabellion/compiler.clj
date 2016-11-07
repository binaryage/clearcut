(ns tabellion.compiler
  "Provides utils for interaction with cljs compiler. Beware! HACKS ahead!"
  (:refer-clojure :exclude [macroexpand])
  (:require [clojure.walk :refer [prewalk]]
            [clojure.set :as set]
            [cljs.analyzer]
            [cljs.closure]
            [cljs.env]
            [tabellion.state :as state]
            [tabellion.messages :refer [messages-registered? register-messages]]
            [tabellion.debug :refer [debug-assert]]))

; -- helpers ----------------------------------------------------------------------------------------------------------------

(defn annotate-with-state [info]
  (assoc info :form tabellion.state/*invocation-form*))

(defn make-slug [type env]
  (list type (:file env) (:line env) (:column env)))

; -- cljs macro expanding ---------------------------------------------------------------------------------------------------

(defn macroexpand* [env form]
  (let [expanded-form (cljs.analyzer/macroexpand-1 env form)]
    (if (identical? form expanded-form)
      expanded-form
      (recur env expanded-form))))

(defn macroexpand-all* [env form]
  (prewalk (fn [x] (if (seq? x) (macroexpand* env x) x)) form))

(defn macroexpand [form]
  (debug-assert tabellion.state/*invocation-env*)
  (macroexpand-all* tabellion.state/*invocation-env* form))

; -- compiler context -------------------------------------------------------------------------------------------------------

(defmacro with-hooked-compiler! [& body]
  `(do
     (if-not (messages-registered? cljs.analyzer/*cljs-warnings*)
       (set! cljs.analyzer/*cljs-warnings* (register-messages cljs.analyzer/*cljs-warnings*)))                                ; add our messages on first invocation
     ~@body))

(defmacro with-compiler-opts! [opts & body]
  `(binding [tabellion.state/*invocation-opts* (merge tabellion.state/*invocation-opts* ~opts)]
     ~@body))

(defmacro with-suppressed-reporting! [messages & body]
  (let [messages-set (set (if (coll? messages) messages (list messages)))]
    `(let [updated-messages-set# (set/union (:suppress-reporting tabellion.state/*invocation-opts*) ~messages-set)]
       (tabellion.compiler/with-compiler-opts! {:suppress-reporting updated-messages-set#}
         ~@body))))

(defmacro with-compiler-diagnostics-context! [form env & body]
  `(binding [tabellion.state/*invocation-form* ~form
             tabellion.state/*invocation-env* ~env]
     ~@body))

(defmacro with-compiler-context! [form env & body]
  `(tabellion.compiler/with-hooked-compiler!
     (tabellion.compiler/with-compiler-diagnostics-context! ~form ~env ~@body)))

; -- subsystem for preventing duplicit warnings -----------------------------------------------------------------------------

(defonce original-build-fn (volatile! nil))

(defn build-wrapper [& args]
  (debug-assert @original-build-fn)
  (if cljs.env/*compiler*
    (swap! cljs.env/*compiler* dissoc ::issued-warnings))
  (apply @original-build-fn args))

(defn install-build-wrapper-if-needed! []
  ; this feels way too hacky, know a better way how to achieve this?
  (if (nil? @original-build-fn)
    (vreset! original-build-fn cljs.closure/build)
    (alter-var-root #'cljs.closure/build (constantly build-wrapper))))

(defn ensure-no-warnings-duplicity! [type env]
  (assert cljs.env/*compiler*)
  (install-build-wrapper-if-needed!)
  (let [slug (make-slug type env)
        issued-warnings (get @cljs.env/*compiler* ::issued-warnings)]
    (when-not (contains? issued-warnings slug)
      (swap! cljs.env/*compiler* update ::issued-warnings #(conj (or % #{}) slug))
      true)))

; -- compile-time warnings and errors ---------------------------------------------------------------------------------------

(defn warn! [type & [info]]
  (assert state/*invocation-env* "tabellion.state/*invocation-env* must be set via with-diagnostics-context! first!")
  (if (ensure-no-warnings-duplicity! type state/*invocation-env*)                                                             ; prevent issuing duplicated warnings (due to double macro analysis under some circumstances)
    (cljs.analyzer/warning type state/*invocation-env* (annotate-with-state info))))

(defn error! [type & [info]]
  (assert state/*invocation-env* "tabellion.state/*invocation-env* must be set via with-diagnostics-context! first!")
  (let [msg (cljs.analyzer/error-message type (annotate-with-state info))]
    (throw (cljs.analyzer/error state/*invocation-env* msg))))
