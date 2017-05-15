(ns clearcut.core
  "The main namespace to be consumend by library users from ClojureScript.
  Provides core macros via core.clj and runtime support (see runtime.clj).
  Read about usage: https://github.com/binaryage/clearcut"
  (:require-macros [clearcut.core]
                   [clearcut.runtime :as runtime])
  (:require [clearcut.config :as config]
            [clearcut.messages :as messages]
            [clearcut.state :as state]
            [clearcut.schema :as schema]
            [clearcut.types :as types]
            [clearcut.helpers :as helpers]))

; -- diagnostics reporting --------------------------------------------------------------------------------------------------

(defn ^:dynamic report-error-dynamically [msg data]
  (runtime/gen-report-error-dynamically msg data))

(defn ^:dynamic report-warning-dynamically [msg data]
  (runtime/gen-report-warning-dynamically msg data))

(defn ^:dynamic report-if-needed-dynamically [msg-id & [info]]
  (runtime/gen-report-if-needed-dynamically msg-id info))

; -- runtime support for macros ---------------------------------------------------------------------------------------------

(defn log-dynamically [level items]
  (runtime/gen-log-dynamically level items))

; -- variadic api -----------------------------------------------------------------------------------------------------------

(defn fatal [& items]
  (runtime/gen-variadic-invoke :fatal items))

(defn error [& items]
  (runtime/gen-variadic-invoke :error items))

(defn warn [& items]
  (runtime/gen-variadic-invoke :warn items))

(defn info [& items]
  (runtime/gen-variadic-invoke :info items))

(defn debug [& items]
  (runtime/gen-variadic-invoke :debug items))

(defn trace [& items]
  (runtime/gen-variadic-invoke :trace items))
