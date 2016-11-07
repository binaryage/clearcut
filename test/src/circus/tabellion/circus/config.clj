(ns tabellion.circus.config
  (:require [tabellion.circus.utils :as utils]))

(defn get-log-level [env]
  (or (:tabellion-log-level env) "INFO"))                                                                                          ; INFO, DEBUG, TRACE, ALL

(defn skip-clean? [env]
  (not (empty? (:tabellion-ft-skip-clean env))))

(defn skip-build? [env build-name]
  (let [filter-str (:tabellion-ft-filter env)
        filter-fn (utils/get-build-filter filter-str)]
    (if (filter-fn build-name)
      (str "env TABELLION_FT_FILTER='" filter-str "'"))))
