(ns clearcut.types)

(deftype Style [css])

(defn make-style [css]
  (Style. css))

(deftype Format [fmtstr])

(defn make-format [fmtstr]
  (Format. fmtstr))
