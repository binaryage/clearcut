(ns tabellion.version
  (:require-macros [tabellion.version :refer [get-current-version]]))

(def current-version (get-current-version))

(defn get-current-version []
  current-version)
