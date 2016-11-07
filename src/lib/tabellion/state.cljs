(ns tabellion.state
  "Here we gather runtime state. For perfomance/code-gen reasons we keep everything under one JS array."
  (:require-macros [tabellion.debug :refer [debug-assert]]
                   [tabellion.constants :as constants])
  (:require [tabellion.helpers :refer [repurpose-error]]
            [tabellion.config :as config]))

(def ^:dynamic *runtime-state*)

; state is a javascript array with following slots:
(debug-assert (= (constants/call-site-error-idx) 0))
(debug-assert (= (constants/console-reporter-idx) 1))
(debug-assert (= (constants/error-reported-idx) 2))

; -- helpers ----------------------------------------------------------------------------------------------------------------

(defn prepare-state [call-site-error console-reporter]
  (array call-site-error
         console-reporter
         false))

(defn get-console-reporter []
  (debug-assert *runtime-state*)
  (let [console-reporter (aget *runtime-state* (constants/console-reporter-idx))]
    (debug-assert (fn? console-reporter))
    console-reporter))

(defn get-call-site-error []
  (debug-assert *runtime-state*)
  (let [call-site-error (aget *runtime-state* (constants/call-site-error-idx))]
    (debug-assert (instance? js/Error call-site-error))
    call-site-error))

(defn ^boolean was-error-reported? []
  (debug-assert *runtime-state*)
  (let [error-reported? (aget *runtime-state* (constants/error-reported-idx))]
    (debug-assert (boolean? error-reported?))
    error-reported?))

(defn mark-error-reported! []
  (debug-assert *runtime-state*)
  (aset *runtime-state* (constants/error-reported-idx) true))

(defn prepare-error-from-call-site [msg data]
  (if (config/throw-errors-from-macro-call-sites?)
    (repurpose-error (get-call-site-error) msg data)
    (js/Error. msg)))                                                                                                         ; this is a fail-safe option for people with repurpose-error-related troubles, we don't attach data in this case
