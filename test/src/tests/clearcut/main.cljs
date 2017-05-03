(ns clearcut.main
  (:require [cljs.test :refer-macros [deftest testing is are run-tests use-fixtures]]
            [cuerdas.core]
            [clearcut.core :as log]
            [clearcut.config :refer [with-runtime-config with-compiler-config update-current-runtime-config!]]
            [clearcut.tools
             :refer [with-captured-console with-main-init setup-runtime-config]
             :refer-macros [presume-compiler-config
                            when-advanced-mode when-not-advanced-mode if-advanced-mode
                            under-phantom
                            under-chrome
                            if-phantom
                            with-console-recording
                            with-stderr-recording
                            when-compiler-config when-not-compiler-config
                            macro-identity
                            logs?]]
            [clojure.string :as string]))

(use-fixtures :once with-main-init with-captured-console (setup-runtime-config {:use-envelope false}))

(deftest test-logging
  (with-compiler-config {:elided-log-levels #{}}
    (testing "simple exercise of all log macros"
      (let [output (atom [])]
        (with-console-recording output
          (log/fatal 1)
          (log/error 2)
          (log/warn 3)
          (log/info 4)
          (log/debug 5)
          (log/trace 6))
        (is (= ["ERROR: (1)" "ERROR: (2)" "WARN: (3)" "INFO: (4)" "LOG: (5)" "LOG: (6)"] @output))))
    (when-not-advanced-mode
      (testing "multiple parameters without formatting"
        (logs? (log/debug "hello" 42 4.2 true :key #"regexp")
               "LOG: (\"hello\" 42 4.2 true :key #\"regexp\")")
        (logs? (log/debug js/window IAtom TransientVector)
               "LOG: (#object[Object [object Window]] #object[Function \"function () {}\"] cljs.core/TransientVector)")))
    (testing "styling"
      (logs? (log/debug (log/style "color:red") "hello")
             "LOG: (\"%c%s\" \"color:red\" \"hello\")")
      (logs? (log/debug "start" (log/style "color:red") "hello" (log/style "color:blue") "world!" "end")
             "LOG: (\"%s %c%s %c%s %s\" \"start\" \"color:red\" \"hello\" \"color:blue\" \"world!\" \"end\")")
      (logs? (log/debug "styling object" (log/style "color:purple") (js-obj))
             "LOG: (\"%s %c%o\" \"styling object\" \"color:purple\" #js {})"))))

(deftest test-eliding
  (testing ":elided-log-levels does compile-time eliding"
    (with-compiler-config {:elided-log-levels #{}}
      (let [output (atom [])]
        (with-console-recording output
          (log/fatal 1)
          (log/error 2)
          (log/warn 3)
          (log/info 4)
          (log/debug 5)
          (log/trace 6))
        (is (= ["ERROR: (1)" "ERROR: (2)" "WARN: (3)" "INFO: (4)" "LOG: (5)" "LOG: (6)"] @output))))
    (with-compiler-config {:elided-log-levels #{1 2 3 4 5 6}}
      (let [output (atom [])]
        (with-console-recording output
          (log/fatal 1)
          (log/error 2)
          (log/warn 3)
          (log/info 4)
          (log/debug 5)
          (log/trace 6))
        (is (= [] @output))))
    (with-compiler-config {:elided-log-levels #{1 3 4}}
      (let [output (atom [])]
        (with-console-recording output
          (log/fatal 1)
          (log/error 2)
          (log/warn 3)
          (log/info 4)
          (log/debug 5)
          (log/trace 6))
        (is (= ["ERROR: (2)" "LOG: (5)" "LOG: (6)"] @output))))))
