(ns tabellion.reporting
  "A subsystem for reporting compile-time issues depending on current config."
  (:refer-clojure :exclude [gensym])
  (:require [tabellion.config :as config]
            [tabellion.compiler :as compiler]
            [tabellion.debug :refer [log debug-assert]]
            [tabellion.state :as state]))

; -- reporting --------------------------------------------------------------------------------------------------------------

(defn supress-reporting? [type]
  (boolean (get-in tabellion.state/*invocation-opts* [:suppress-reporting type])))

(defn report! [type & [info]]
  (case (config/get-config-key type)
    :warn (compiler/warn! type info)
    :error (compiler/error! type info)
    (false nil) nil))

(defn report-if-needed! [type & [info]]
  (if (config/diagnostics?)
    (if-not (supress-reporting? type)
      (report! type info))))

(defn report-offending-selector-if-needed! [offending-selector type & [info]]
  (debug-assert offending-selector)
  (if (config/diagnostics?)
    (if-not (supress-reporting? type)
      (let [point-to-offending-selector (into {} (filter second (select-keys (meta offending-selector) [:line :column])))]
        ; note that sometimes param meta could be missing, we don't alter state/*invocation-env* in that case
        (binding [state/*invocation-env* (merge state/*invocation-env* point-to-offending-selector)]
          (report! type info))))))
