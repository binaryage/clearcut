(ns clearcut.schema
  (:require-macros [clearcut.schema])
  (:require [clearcut.helpers :as helpers]))

; -- dynamic formatting of log args -----------------------------------------------------------------------------------------
; keep this in sync with static version!

(defn formatted? [o]
  (or (helpers/style? o)
      (helpers/format? o)))

(defn prepare-formatted-log-args [items-array]
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
            (helpers/format? item) (recur (next items) (str "%" (.-fmtstr item)))
            :else (do
                    (.push format (or carry (if (string? item) "%s " "%o ")))
                    (.push result item)
                    (recur (next items) nil))))
        (do
          (.unshift result (.trim (.join format "")))
          result)))))

(defn prepare-log-args [items-array]
  (if (some formatted? items-array)
    (prepare-formatted-log-args items-array)
    items-array))
