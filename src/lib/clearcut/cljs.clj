(ns clearcut.cljs
  "Functions for interop with ClojureScript library.

  When using clearcut from Clojure we don't want to introduce a hard dependency on ClojureScript.
  Solution is to resolve ClojureScript API dynamically and use it only if available."
  (:refer-clojure :exclude [macroexpand])
  (:require [clojure.walk :refer [prewalk]]
            [clearcut.state :as state]))

; -- cljs.env ---------------------------------------------------------------------------------------------------------------

(def cljs-env-ns-sym 'cljs.env)

(defn try-resolve-cljs-env-ns-symbol* [sym]
  (try
    (require cljs-env-ns-sym)
    (ns-resolve cljs-env-ns-sym sym)
    (catch Throwable e
      nil)))

(def try-resolve-cljs-env-ns-symbol (memoize try-resolve-cljs-env-ns-symbol*))

(defn try-resolve-cljs-env-var* [sym]
  (let [v (try-resolve-cljs-env-ns-symbol sym)]
    (if (var? v)
      (var-get v))))

(def try-resolve-cljs-env-var (memoize try-resolve-cljs-env-var*))

(defn get-cljs-env-compiler []
  (try-resolve-cljs-env-var '*compiler*))

; -- cljs.analyzer ----------------------------------------------------------------------------------------------------------

(def cljs-analyzer-ns-sym 'cljs.analyzer)

(defn try-resolve-cljs-analyzer-ns-symbol* [sym]
  (try
    (require cljs-analyzer-ns-sym)
    (ns-resolve cljs-analyzer-ns-sym sym)
    (catch Throwable e
      nil)))

(def try-resolve-cljs-analyzer-ns-symbol (memoize try-resolve-cljs-analyzer-ns-symbol*))

(defn try-resolve-cljs-analyzer-var* [sym]
  (let [v (try-resolve-cljs-analyzer-ns-symbol sym)]
    (if (var? v)
      (var-get v))))

(def try-resolve-cljs-analyzer-var (memoize try-resolve-cljs-analyzer-var*))

; -- cljs.closure ----------------------------------------------------------------------------------------------------------

(def cljs-closure-ns-sym 'cljs.closure)

(defn try-resolve-cljs-closure-ns-symbol* [sym]
  (try
    (require cljs-closure-ns-sym)
    (ns-resolve cljs-closure-ns-sym sym)
    (catch Throwable e
      nil)))

(def try-resolve-cljs-closure-ns-symbol (memoize try-resolve-cljs-closure-ns-symbol*))

(defn try-resolve-cljs-closure-var* [sym]
  (let [v (try-resolve-cljs-closure-ns-symbol sym)]
    (if (var? v)
      (var-get v))))

(def try-resolve-cljs-closure-var (memoize try-resolve-cljs-closure-var*))

; -- helpers ----------------------------------------------------------------------------------------------------------------

(defn annotate-with-state [info]
  (assoc info :form clearcut.state/*invocation-form*))

(defn make-slug [type env]
  (list type (:file env) (:line env) (:column env)))

; -- cljs macro expanding ---------------------------------------------------------------------------------------------------

(defn macroexpand* [env form]
  (let [macroexpand-1 (try-resolve-cljs-analyzer-var 'macroexpand-1)
        expanded-form (macroexpand-1 env form)]
    (if (identical? form expanded-form)
      expanded-form
      (recur env expanded-form))))

(defn macroexpand-all* [env form]
  (prewalk (fn [x] (if (seq? x) (macroexpand* env x) x)) form))

(defn macroexpand [form]
  (assert clearcut.state/*invocation-env*)
  (macroexpand-all* clearcut.state/*invocation-env* form))

; -- subsystem for preventing duplicit warnings -----------------------------------------------------------------------------

(defonce original-build-fn (volatile! nil))

(defn build-wrapper [& args]
  (assert @original-build-fn)
  (if-let [compiler (get-cljs-env-compiler)]
    (swap! compiler dissoc ::issued-warnings))
  (apply @original-build-fn args))

(defn install-build-wrapper-if-needed! []
  ; this feels way too hacky, know a better way how to achieve this?
  (if (nil? @original-build-fn)
    (let [build-var (try-resolve-cljs-closure-ns-symbol 'build)]
      (assert (var? build-var))
      (vreset! original-build-fn (var-get build-var))
      (alter-var-root build-var (constantly build-wrapper)))))

(defn ensure-no-warnings-duplicity! [type env]
  (let [compiler (get-cljs-env-compiler)]
    (assert compiler)
    (install-build-wrapper-if-needed!)
    (let [slug (make-slug type env)
          issued-warnings (get @compiler ::issued-warnings)]
      (when-not (contains? issued-warnings slug)
        (swap! compiler update ::issued-warnings #(conj (or % #{}) slug))
        true))))

; -- compile-time warnings and errors ---------------------------------------------------------------------------------------

(defn clojurescript-lib-present? []
  (boolean (try-resolve-cljs-env-ns-symbol)))

(defn warn! [type & [info]]
  (assert (clojurescript-lib-present?))
  (assert state/*invocation-env* "clearcut.state/*invocation-env* must be set via with-diagnostics-context! first!")
  (if (ensure-no-warnings-duplicity! type state/*invocation-env*)                                                             ; prevent issuing duplicated warnings (due to double macro analysis under some circumstances)
    (let [warning-fn (try-resolve-cljs-analyzer-var 'warning)]
      (assert (fn? warning-fn))
      (warning-fn type state/*invocation-env* (annotate-with-state info)))))

(defn error! [type & [info]]
  (assert (clojurescript-lib-present?))
  (assert state/*invocation-env* "clearcut.state/*invocation-env* must be set via with-diagnostics-context! first!")
  (let [error-message-fn (try-resolve-cljs-analyzer-var 'error-message)
        error-fn (try-resolve-cljs-analyzer-var 'error)
        msg (error-message-fn type (annotate-with-state info))]
    (throw (error-fn state/*invocation-env* msg))))
