(ns clearcut.tests.clojure
  (:require [clojure.test :refer :all]
            [clojure.string :as string]
            [clearcut.core :as log]))

(deftest test-basic
  (testing "basic exercise"
    (log/fatal 1)
    (log/error 2)
    (log/warn 3)
    (log/info 4)
    (log/debug 5)
    (log/trace 6)))
