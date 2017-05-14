(ns clearcut.compiler
  "Provides utils for interaction with cljs compiler. Beware! HACKS ahead!"
  (:refer-clojure :exclude [macroexpand])
  (:require [clojure.walk :refer [prewalk]]
            [clojure.set :as set]
            [cljs.analyzer]
            [cljs.closure]
            [cljs.env]
            [clearcut.helpers :as helpers]
            [clearcut.state :as state]
            [clearcut.messages :refer [messages-registered? register-messages]]
            [clearcut.debug :refer [debug-assert]]
            [clearcut.cljs :as cljs]))

; -- cljs macro expanding ---------------------------------------------------------------------------------------------------

(defn macroexpand* [env form]
  (let [expanded-form (cljs.analyzer/macroexpand-1 env form)]
    (if (identical? form expanded-form)
      expanded-form
      (recur env expanded-form))))

(defn macroexpand-all* [env form]
  (prewalk (fn [x] (if (seq? x) (macroexpand* env x) x)) form))

(defn macroexpand [form]
  (debug-assert clearcut.state/*invocation-env*)
  (macroexpand-all* clearcut.state/*invocation-env* form))

; -- compiler context -------------------------------------------------------------------------------------------------------

(defmacro with-hooked-compiler! [env & body]
  `(do
     ~(if (helpers/cljs? env)
        `(if-not (messages-registered? cljs.analyzer/*cljs-warnings*)
           (set! cljs.analyzer/*cljs-warnings* (register-messages cljs.analyzer/*cljs-warnings*))))                           ; add our messages on first invocation
     ~@body))

(defmacro with-compiler-opts! [opts & body]
  `(binding [clearcut.state/*invocation-opts* (merge clearcut.state/*invocation-opts* ~opts)]
     ~@body))

(defmacro with-suppressed-reporting! [messages & body]
  (let [messages-set (set (if (coll? messages) messages (list messages)))]
    `(let [updated-messages-set# (set/union (:suppress-reporting clearcut.state/*invocation-opts*) ~messages-set)]
       (clearcut.compiler/with-compiler-opts! {:suppress-reporting updated-messages-set#}
         ~@body))))

(defmacro with-compiler-diagnostics-context! [form env & body]
  `(binding [clearcut.state/*invocation-form* ~form
             clearcut.state/*invocation-env* ~env]
     ~@body))

(defmacro with-compiler-context! [form env & body]
  `(clearcut.compiler/with-hooked-compiler! env
     (clearcut.compiler/with-compiler-diagnostics-context! ~form ~env ~@body)))

; -- compile-time warnings and errors ---------------------------------------------------------------------------------------

(defn warn! [type & [info]]
  (assert state/*invocation-env* "clearcut.state/*invocation-env* must be set via with-diagnostics-context! first!")
  (if (helpers/cljs? state/*invocation-env*)
    (cljs/warn! type info)
    (assert "implement Clojure version")))                                                                                    ; TODO: Clojure path

(defn error! [type & [info]]
  (assert state/*invocation-env* "clearcut.state/*invocation-env* must be set via with-diagnostics-context! first!")
  (if (helpers/cljs? state/*invocation-env*)
    (cljs/error! type info)
    (assert "implement Clojure version")))                                                                                    ; TODO: Clojure path
