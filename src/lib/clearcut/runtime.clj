(ns clearcut.runtime
  "Macros for generating runtime support code. Generated functions are located in clearcut.core namespace."
  (:refer-clojure :exclude [gensym])
  (:require [clearcut.config :as config]
            [clearcut.codegen :refer [gen-log-with-env level-to-method]]
            [clearcut.constants :as constants]
            [clearcut.debug :refer [log debug-assert]]
            [clearcut.helpers :as helpers :refer [gensym]]
            [clearcut.clojure :as clearcut-clojure]
            [clearcut.types :as types])
  (:import (clearcut.types Style Format)))

; -- error reporting code ---------------------------------------------------------------------------------------------------

(defn gen-supress-reporting? [msg-id]
  `(contains? (clearcut.config/get-suppress-reporting) ~msg-id))

(defn gen-reported-message [msg]
  msg)

(defn gen-reported-data [data]
  `(let [data# ~data]
     (or (if (clearcut.config/use-envelope?)
           (if-let [devtools# (cljs.core/aget goog/global "devtools")]
             (if-let [toolbox# (cljs.core/aget devtools# "toolbox")]
               (if-let [envelope# (cljs.core/aget toolbox# "envelope")]
                 (if (cljs.core/fn? envelope#)
                   (envelope# data# "details"))))))
         data#)))

(defn gen-console-method [kind]
  (case kind
    :error `(aget js/console "error")
    :warning `(aget js/console "warn")))

(defn gen-report-runtime-message [kind msg data]
  (debug-assert (contains? #{:error :warning} kind))
  (let [mode (case kind
               :error `(clearcut.config/get-error-reporting)
               :warning `(clearcut.config/get-warning-reporting))]
    `(case ~mode
       :throw (throw (clearcut.state/prepare-error-from-call-site ~(gen-reported-message msg) ~(gen-reported-data data)))
       :console ((clearcut.state/get-console-reporter) ~(gen-console-method kind) ~(gen-reported-message msg) ~(gen-reported-data data))
       false nil)))

(defn gen-report-error-dynamically [msg data]
  `(when-not (clearcut.state/was-error-reported?)                                                                             ; we want to print only first error for single invocation
     (clearcut.state/mark-error-reported!)
     ~(gen-report-runtime-message :error msg data)))

(defn gen-report-warning-dynamically [msg data]
  (gen-report-runtime-message :warning msg data))

(defn gen-report-dynamically [msg-id info-sym]
  `(do
     (debug-assert (clearcut.config/has-config-key? ~msg-id) (str "runtime config has missing key: " ~msg-id))
     (if-not ~(gen-supress-reporting? msg-id)
       (case (clearcut.config/get-config-key ~msg-id)
         :warn (clearcut.core/report-warning-dynamically (clearcut.messages/runtime-message ~msg-id ~info-sym) ~info-sym)
         :error (clearcut.core/report-error-dynamically (clearcut.messages/runtime-message ~msg-id ~info-sym) ~info-sym)
         (false nil) nil))
     nil))

; -- dynamic log ------------------------------------------------------------------------------------------------------------

(defn gen-level-to-method [level]
  (debug-assert (= (count constants/all-levels) 6))
  `(case ~level
     ~constants/level-fatal ~(level-to-method constants/level-fatal)
     ~constants/level-error ~(level-to-method constants/level-error)
     ~constants/level-warn ~(level-to-method constants/level-warn)
     ~constants/level-info ~(level-to-method constants/level-info)
     ~constants/level-debug ~(level-to-method constants/level-debug)
     ~constants/level-trace ~(level-to-method constants/level-trace)))

(defn gen-log-call-with-diagnostics [level items-array]
  `(let [method# ~(gen-level-to-method level)
         args-array# (clearcut.schema/prepare-log-args ~items-array)
         method+args# (.concat (cljs.core/array method#) args-array#)]
     (.apply (clearcut.state/get-console-reporter) nil method+args#)))

(defn gen-log-call-without-diagnostics [level items-array]
  `(let [method# ~(gen-level-to-method level)
         args-array# (clearcut.schema/prepare-log-args ~items-array)]
     (.apply method# nil args-array#)))

(defn gen-log-dynamically [level items-array]
  (if (config/diagnostics?)
    (gen-log-call-with-diagnostics level items-array)
    (gen-log-call-without-diagnostics level items-array)))

; -- macros -----------------------------------------------------------------------------------------------------------------

(defmacro report-error-dynamically [msg data]
  (gen-report-error-dynamically msg data))

(defmacro report-warning-dynamically [msg data]
  (gen-report-warning-dynamically msg data))

(defmacro report-dynamically-if-needed [msg-id info-sym]
  (debug-assert (symbol? info-sym))
  (if (config/diagnostics?)
    (gen-report-dynamically msg-id info-sym)))

(defmacro log-dynamically [level items-array-sym]
  (debug-assert (symbol? items-array-sym))
  (gen-log-dynamically level items-array-sym))

(defmacro log-variadic [level-key items]
  (gen-log-with-env &form &env (level-key constants/levels) items))
