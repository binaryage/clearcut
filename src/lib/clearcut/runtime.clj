(ns clearcut.runtime
  "Macros for generating runtime support code.
  Generated functions are located in clearcut.core namespace and have '-dynamically' postix to clearly distinguish them from
  compile-time code."
  (:refer-clojure :exclude [gensym])
  (:require [clearcut.config :as config]
            [clearcut.codegen :refer :all]
            [clearcut.helpers :refer [gensym]]
            [clearcut.constants :as constants]
            [clearcut.debug :refer [log debug-assert]]
            [clearcut.helpers :as helpers]
            [clearcut.clojure :as clearcut-clojure]
            [clearcut.types :as types])
  (:import (clearcut.types Style Format)))

(defmacro gen-report-error-dynamically [msg data]
  `(when-not (clearcut.state/was-error-reported?)                                                                             ; we want to print only first error for single invocation
     (clearcut.state/mark-error-reported!)
     ~(gen-report-runtime-message :error msg data)))

(defmacro gen-report-warning-dynamically [msg data]
  (gen-report-runtime-message :warning msg data))

(defmacro gen-report-if-needed-dynamically [msg-id info-sym]
  (debug-assert (symbol? info-sym))
  (if (config/diagnostics?)
    `(do
       (debug-assert (clearcut.config/has-config-key? ~msg-id) (str "runtime config has missing key: " ~msg-id))
       (if-not ~(gen-supress-reporting? msg-id)
         (case (clearcut.config/get-config-key ~msg-id)
           :warn (clearcut.core/report-warning-dynamically (clearcut.messages/runtime-message ~msg-id ~info-sym) ~info-sym)
           :error (clearcut.core/report-error-dynamically (clearcut.messages/runtime-message ~msg-id ~info-sym) ~info-sym)
           (false nil) nil))
       nil)))

(defmacro gen-log-dynamically [level items-array-sym]
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
