(ns clearcut.core
  "The main namespace to be consumend by library users from ClojureScript.
  Provides core macros via core.clj and runtime support (see runtime.clj).
  Read about usage: https://github.com/binaryage/clearcut"
  (:require-macros [clearcut.core :refer [gen-variadic-invoke]])
  (:require [clearcut.state]
            [clearcut.config]
            [clearcut.messages]
            [clearcut.runtime :as runtime]
            [clearcut.types :as types]
            [clearcut.helpers :as helpers]))

; -- helpers ----------------------------------------------------------------------------------------------------------------

(defn style [css]
  (types/make-style css))

(defn format [fmtstr]
  (types/make-format fmtstr))

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
  (gen-variadic-invoke :fatal items))

(defn error [& items]
  (gen-variadic-invoke :error items))

(defn warn [& items]
  (gen-variadic-invoke :warn items))

(defn info [& items]
  (gen-variadic-invoke :info items))

(defn debug [& items]
  (gen-variadic-invoke :debug items))

(defn trace [& items]
  (gen-variadic-invoke :trace items))
