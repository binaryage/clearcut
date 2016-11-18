(ns clearcut.version
  (:require-macros [clearcut.version :refer [get-current-version]]))

(def current-version (get-current-version))

(defn get-current-version []
  current-version)
