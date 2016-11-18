(ns clearcut.runner
  (:require [environ.core :refer [env]]))

(defmacro ansi-enabled? []
  (not (:clearcut-disable-test-runner-ansi env)))
