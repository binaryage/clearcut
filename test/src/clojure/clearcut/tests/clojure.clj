(ns clearcut.tests.clojure
  (:require [clojure.test :refer :all]
            [clojure.string :as string]
            [clearcut.core :as log]
            [clearcut.types :refer [make-format make-style]]))

(deftest test-basic
  (testing "basic exercise"
    (log/fatal 1)
    (log/error 2)
    (log/warn 3)
    (log/info 4)
    (log/debug 5)
    (log/trace 6))
  (testing "elide formatting/styling"
    (log/fatal (make-format "f") 1 (make-style "css") 2 (make-format "f") 3 (make-style "css") 4)))
