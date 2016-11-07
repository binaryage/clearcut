(ns tabellion.runner
  (:require [environ.core :refer [env]]))

(defmacro ansi-enabled? []
  (not (:tabellion-disable-test-runner-ansi env)))
