(ns clearcut.arena.log-dev
  (:refer-clojure :exclude [format])
  (:require-macros [clearcut.arena.macros :refer [macro-identity]])
  (:require [clearcut.core :refer [info style format]]
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

(testing "trivial static logging"
  (info "output"))

(testing "simple static logging"
  (info "s" nil 42 4.2 true :key))

(testing "static logging with formatting"
  (info "s" (style "color:red") "xxx" (format "o")))

(testing "static logging with symbols"
  (let [val 42]
    (info val)))
