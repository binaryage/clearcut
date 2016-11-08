(ns tabellion.compiler
  "Provides utils for interaction with cljs compiler. Beware! HACKS ahead!"
  (:refer-clojure :exclude [macroexpand])
  (:require [clojure.walk :refer [prewalk]]
            [clojure.set :as set]
            [tabellion.helpers :as helpers]
            [tabellion.state :as state]
            [tabellion.messages :refer [messages-registered? register-messages]]
            [tabellion.debug :refer [debug-assert]]
            [tabellion.cljs :as cljs]))

; -- compiler context -------------------------------------------------------------------------------------------------------

(defmacro with-hooked-compiler! [env & body]
  `(do
     ~(if (helpers/cljs? env)
        `(if-not (messages-registered? cljs.analyzer/*cljs-warnings*)
           (set! cljs.analyzer/*cljs-warnings* (register-messages cljs.analyzer/*cljs-warnings*))))                           ; add our messages on first invocation
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
  `(tabellion.compiler/with-hooked-compiler! env
     (tabellion.compiler/with-compiler-diagnostics-context! ~form ~env ~@body)))

; -- compile-time warnings and errors ---------------------------------------------------------------------------------------

(defn warn! [type & [info]]
  (assert state/*invocation-env* "tabellion.state/*invocation-env* must be set via with-diagnostics-context! first!")
  (if (helpers/cljs? state/*invocation-env*)
    (cljs/warn! type info)
    (assert "implement Clojure version")))                                                                                    ; TODO: Clojure path

(defn error! [type & [info]]
  (assert state/*invocation-env* "tabellion.state/*invocation-env* must be set via with-diagnostics-context! first!")
  (if (helpers/cljs? state/*invocation-env*)
    (cljs/error! type info)
    (assert "implement Clojure version")))                                                                                    ; TODO: Clojure path
