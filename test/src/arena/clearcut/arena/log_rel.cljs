(ns clearcut.arena.log-rel
  (:refer-clojure :exclude [format])
  (:require-macros [clearcut.arena.macros :refer [macro-identity]])
  (:require [clearcut.core :refer [fatal error warn info debug trace style format]]
            [clearcut.tools :refer [init-arena-test! testing]]
            [clearcut.config :refer [with-compiler-config]]))

(init-arena-test!)

; we want to test generated code shape expansion under dev mode

(testing "exercise all without args"
  (with-compiler-config {:elided-log-levels nil}
    (fatal)
    (error)
    (warn)
    (info)
    (debug)
    (trace)))

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

(testing "static logging with macro-expansion"
  (info (macro-identity "s") (macro-identity 42) (macro-identity (macro-identity :key))))
