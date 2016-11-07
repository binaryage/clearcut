(ns tabellion.types)

(deftype Style [css])

(defn make-style [css]
  (Style. css))

(deftype Format [format-string])

(defn make-format [format-string]
  (Format. format-string))
