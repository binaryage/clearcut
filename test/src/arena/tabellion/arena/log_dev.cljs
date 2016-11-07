(ns tabellion.arena.log-dev
  (:require-macros [tabellion.arena.macros :refer [macro-identity]])
  (:require [tabellion.core :refer [info]]
            [tabellion.config :refer [without-diagnostics with-debug]]
            [tabellion.tools :refer [init-arena-test! testing]]))

(init-arena-test!)

; we want to test generated code shape expansion under dev mode

(testing "log/info"
  (info 1 :key (js-obj)))
