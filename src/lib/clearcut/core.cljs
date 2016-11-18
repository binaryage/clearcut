(ns clearcut.core
  "The main namespace to be consumend by library users from ClojureScript.
  Provides core macros via core.clj and runtime support (see runtime.clj).
  Read about usage: https://github.com/binaryage/clearcut"
  (:require-macros [clearcut.core])
  (:require [clearcut.state]
            [clearcut.config]
            [clearcut.messages]
            [clearcut.runtime :as runtime]
            [clearcut.types :as types]
            [clearcut.helpers :as helpers]))

; -- helpers ----------------------------------------------------------------------------------------------------------------

(defn style [css]
  (types/make-style css))

(defn format [format-string]
  (types/make-format format-string))

; -- diagnostics reporting --------------------------------------------------------------------------------------------------

(defn ^:dynamic report-error-dynamically [msg data]
  (runtime/report-error-dynamically msg data))

(defn ^:dynamic report-warning-dynamically [msg data]
  (runtime/report-warning-dynamically msg data))

(defn ^:dynamic report-if-needed-dynamically [msg-id & [info]]
  (runtime/report-if-needed-dynamically msg-id info))

; -- runtime support for macros ---------------------------------------------------------------------------------------------

(defn log-dynamically [level items]
  (runtime/log-dynamically level items))
