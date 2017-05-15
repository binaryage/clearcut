(ns clearcut.debug
  "Debug utils."
  (:require [clojure.pprint]
            [clojure.string :as string]
            [clearcut.log]
            [clearcut.config :as config]))

(def log clearcut.log/log)

(defmacro debug-assert [& args]
  (if (config/debug?)
    `(assert ~@args)))
