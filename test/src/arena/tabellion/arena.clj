(ns tabellion.arena
  (:require [clojure.test :refer :all]
            [tabellion.circus.build :refer [get-key-mode-options make-build exercise-builds!]]))

(def all-builds
  (concat
    [(make-build "log_dev.cljs" "" {} {:optimizations :whitespace})]))

; -- tests ------------------------------------------------------------------------------------------------------------------

(deftest exercise-all-builds
  (exercise-builds! all-builds))
