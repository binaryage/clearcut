(ns tabellion.runtime
  "Macros for generating runtime support code.
  Generated functions are located in tabellion.core namespace and have '-dynamically' postix to clearly distinguish them from
  compile-time code."
  (:refer-clojure :exclude [gensym])
  (:require [tabellion.config :as config]
            [tabellion.codegen :refer :all]
            [tabellion.helpers :refer [gensym]]
            [tabellion.constants :as constants]
            [tabellion.debug :refer [log debug-assert]]))

(defmacro report-error-dynamically [msg data]
  `(when-not (tabellion.state/was-error-reported?)                                                                            ; we want to print only first error for single invocation
     (tabellion.state/mark-error-reported!)
     ~(gen-report-runtime-message :error msg data)))

(defmacro report-warning-dynamically [msg data]
  (gen-report-runtime-message :warning msg data))

(defmacro report-if-needed-dynamically [msg-id info-sym]
  (debug-assert (symbol? info-sym))
  (if (config/diagnostics?)
    `(do
       (debug-assert (tabellion.config/has-config-key? ~msg-id) (str "runtime config has missing key: " ~msg-id))
       (if-not ~(gen-supress-reporting? msg-id)
         (case (tabellion.config/get-config-key ~msg-id)
           :warn (tabellion.core/report-warning-dynamically (tabellion.messages/runtime-message ~msg-id ~info-sym) ~info-sym)
           :error (tabellion.core/report-error-dynamically (tabellion.messages/runtime-message ~msg-id ~info-sym) ~info-sym)
           (false nil) nil))
       nil)))

(defmacro log-dynamically [level items-array-sym]
  (debug-assert (symbol? items-array-sym))
  `(log! ~level ~items-array-sym))

(defmacro gen-level-to-method [level]
  `(case ~level
     ~constants/level-fatal js/console.error
     ~constants/level-error js/console.error
     ~constants/level-warn js/console.warn
     ~constants/level-info js/console.info
     ~constants/level-debug js/console.log
     ~constants/level-trace js/console.log))
