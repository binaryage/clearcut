(ns clearcut.runtime
  (:require-macros [clearcut.runtime :as runtime]
                   [clearcut.constants :as constants])
  (:require [clearcut.helpers :as helpers]
            [clojure.string :as string]
            [clearcut.state :as state]
            [clearcut.schema :as schema]))

(defn level-to-method [level]
  (runtime/gen-level-to-method level))

(defn log! [level items-array]
  (let [method (level-to-method level)
        args-array (schema/prepare-log-args items-array)
        method+args (.concat #js [method] args-array)
        reporter (state/get-console-reporter)]
    ; TODO: generalize it here for advanced builds where we want to emit direct code
    (.apply reporter nil method+args)))
