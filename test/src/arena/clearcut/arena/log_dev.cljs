(ns clearcut.arena.log-dev
  (:require-macros [clearcut.arena.macros :refer [macro-identity]])
  (:require [clearcut.core :refer [info]]
            [clearcut.config :refer [without-diagnostics with-debug]]
            [clearcut.tools :refer [init-arena-test! testing]]))

(init-arena-test!)

; we want to test generated code shape expansion under dev mode

(testing "log/info"
  (info 1 :key (js-obj)))

(testing "variadic log/info via apply"
  (apply info ["hello," "world!"]))

(testing "variadic log/info via .call"
  (.call info nil "hello," "world!"))

(testing "variadic log/info via .apply"
  (.apply info nil #js ["hello," "world!"]))
