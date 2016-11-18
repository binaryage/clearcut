(ns clearcut.helpers
  "Various helpers to be avaialbe to our code at runtime."
  (:require-macros [clearcut.helpers]
                   [clearcut.debug :refer [debug-assert]])
  (:require [clearcut.types :refer [Style Format]]))

(defn repurpose-error [error msg info]
  (debug-assert (instance? js/Error error))
  (debug-assert (string? msg))
  (set! (.-message error) msg)
  (specify! error
    IPrintWithWriter                                                                                                          ; nice to have for cljs-devtools and debug printing
    (-pr-writer [_obj writer opts]
      (-write writer msg)
      (when (some? info)
        (-write writer " ")
        (pr-writer info writer opts)))))

(defn style? [o]
  (instance? Style o))

(defn format? [o]
  (instance? Format o))
