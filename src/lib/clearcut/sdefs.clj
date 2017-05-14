(ns clearcut.sdefs
  "Spec definitions for our static code."
  (:require [clojure.spec.alpha :as s]))

; --- helpers ---------------------------------------------------------------------------------------------------------------

(defn valid-fmtstr? [fmtstr]
  ; https://github.com/GoogleChrome/devtools-docs/blob/1a41b5540edd63ef7899f3f2cb5b37b96c316df0/docs/console-api.md#consolelogobject--object-
  (case fmtstr
    ("s" "d" "i" "f" "o" "O" "c") true
    false))

(defn valid-style? [style]
  (string? style))

(defn format-ctor? [x]
  (= 'clearcut.types/->Format x))

(defn style-ctor? [x]
  (= 'clearcut.types/->Style x))

(defn bool? [x]
  (or (true? x)
      (false? x)))

(defn native? [x]
  (or (string? x)
      (nil? x)
      (bool? x)                                                                                                               ; TODO: use boolean? when we drop Clojure 1.8 support
      (keyword? x)
      (number? x)
      (symbol? x)
      ; TODO: add regex and other known simple types
      ; TODO: maybe think about relaxing this -> native is anything without function calls
      ))

; --- specs -----------------------------------------------------------------------------------------------------------------

(s/def ::valid-fmtstr valid-fmtstr?)
(s/def ::valid-style valid-style?)

(s/def ::format (s/cat :ctor format-ctor? :fmtstr ::valid-fmtstr))
(s/def ::style (s/cat :ctor style-ctor? :style ::valid-style))
(s/def ::native native?)

(s/def ::static-param (s/or :native-param ::native
                            :format-param ::format
                            :style-param ::style))

(s/def ::static-params (s/* ::static-param))
