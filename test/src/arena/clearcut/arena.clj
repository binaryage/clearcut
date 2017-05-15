(ns clearcut.arena
  (:require [clojure.test :refer :all]
            [clearcut.circus.build :refer [get-key-mode-options make-build exercise-builds!]]))

(def all-builds
  (concat
    [(make-build "log_dev.cljs" "" {} {:optimizations :whitespace})
     (make-build "log_rel.cljs" "")]))

; -- tests ------------------------------------------------------------------------------------------------------------------

(deftest exercise-all-builds
  (exercise-builds! all-builds))
