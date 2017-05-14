(ns clearcut.runtime
  (:require-macros [clearcut.runtime :as runtime]
                   [clearcut.constants :as constants])
  (:require [clearcut.helpers :as helpers]
            [clojure.string :as string]
            [clearcut.state :as state]))

(defn level-to-method [level]
  (runtime/gen-level-to-method level))

(defn formatted? [o]
  (or (helpers/style? o)
      (helpers/format? o)))

(defn prepare-format-string [items-array]
  (let [result (array)
        format (array)]
    (loop [items (seq items-array)
           carry nil]
      (if-not (nil? items)
        (let [item (-first items)]
          (cond
            (helpers/style? item) (do
                                    (.push format "%c")
                                    (.push result (.-css item))
                                    (recur (next items) nil))
            (helpers/format? item) (recur (next items) (.-fmtstr item))
            :else (do
                    (.push format (or carry (if (string? item) "%s " "%o ")))
                    (.push result item)
                    (recur (next items) nil))))
        (do
          (.unshift result (.trim (.join format "")))
          result)))))

(defn prepare-log-args [items-array]
  (if (some formatted? items-array)
    (prepare-format-string items-array)
    (.slice items-array)))

(defn log! [level items-array]
  (let [method (level-to-method level)
        args! (prepare-log-args items-array)
        reporter (state/get-console-reporter)]
    ; note that args is a newly created array so we are free to alter it
    (.unshift args! method)
    (.apply reporter nil args!)))
