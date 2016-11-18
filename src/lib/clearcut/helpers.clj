(ns clearcut.helpers
  "Various helpers for our Clojure code."
  (:refer-clojure :exclude [gensym])
  (:require [cuerdas.core :as cuerdas]
            [clojure.pprint :refer [pprint]]
            [clearcut.constants :as constants]))

(defn indent-text [s count]
  (let [prefix (cuerdas/repeat " " count)]
    (->> s
         (cuerdas/lines)
         (map #(str prefix %))
         (cuerdas/unlines))))

(defn pprint-code-str [code]
  (with-out-str
    (binding [clojure.pprint/*print-right-margin* 200
              *print-length* 100
              *print-level* 5]
      (pprint code))))

(defmacro gensym [name]
  `(clojure.core/gensym (str ~name "-")))

(defn cljs? [env]
  (boolean (:ns env)))

(defn level-to-clojure-logging-method-symbol [level]
  (assert (contains? constants/all-levels level))
  (condp = level
    constants/level-fatal 'clojure.tools.logging/fatal
    constants/level-error 'clojure.tools.logging/error
    constants/level-warn 'clojure.tools.logging/warn
    constants/level-info 'clojure.tools.logging/info
    constants/level-debug 'clojure.tools.logging/debug
    constants/level-trace 'clojure.tools.logging/trace))
