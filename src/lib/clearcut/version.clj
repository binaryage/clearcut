(ns clearcut.version)

(def current-version "0.1.0-SNAPSHOT")                                                                                        ; this should match our project.clj

(defmacro get-current-version []
  current-version)
