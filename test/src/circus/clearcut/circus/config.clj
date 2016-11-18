(ns clearcut.circus.config
  (:require [clearcut.circus.utils :as utils]))

(defn get-log-level [env]
  (or (:clearcut-log-level env) "INFO"))                                                                                          ; INFO, DEBUG, TRACE, ALL

(defn skip-clean? [env]
  (not (empty? (:clearcut-ft-skip-clean env))))

(defn skip-build? [env build-name]
  (let [filter-str (:clearcut-ft-filter env)
        filter-fn (utils/get-build-filter filter-str)]
    (if (filter-fn build-name)
      (str "env CLEARCUT_FT_FILTER='" filter-str "'"))))
