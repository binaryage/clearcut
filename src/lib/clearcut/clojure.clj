(ns clearcut.clojure
  "Functions for interop with clojure.tools.logging library.

  When using clearcut from ClojureScript-only we don't want to introduce a hard dependency on Clojure's clojure.tools.logging.
  Solution is to resolve Clojure API dynamically and use it only if available."
  (:require [clearcut.helpers :as helpers]
            [clearcut.types])
  (:import (clearcut.types Format Style)))

; -- clojure.tools.logging --------------------------------------------------------------------------------------------------

(def clojure-tools-logging-ns-sym 'clojure.tools.logging)

(defn try-resolve-clojure-tools-logging-ns-symbol* [sym]
  (try
    (require clojure-tools-logging-ns-sym)
    (ns-resolve clojure-tools-logging-ns-sym sym)
    (catch Throwable e
      (throw (ex-info (str "clearcut: Unable to require " clojure-tools-logging-ns-sym ".") {} e)))))

(def try-resolve-clojure-tools-logging-ns-symbol (memoize try-resolve-clojure-tools-logging-ns-symbol*))

(defn try-resolve-clojure-tools-logging-var* [sym]
  (let [v (try-resolve-clojure-tools-logging-ns-symbol sym)]
    (if (var? v)
      (var-get v)
      (throw (ex-info (str "clearcut: Unable to resolve var " sym " in " clojure-tools-logging-ns-sym ".") {})))))

(def try-resolve-clojure-tools-logging-var (memoize try-resolve-clojure-tools-logging-var*))

(defn get-clojure-tools-logging-logger-factory []
  (try-resolve-clojure-tools-logging-var '*logger-factory*))

(defn get-clojure-tools-logging-log* []
  (try-resolve-clojure-tools-logging-var 'log*))

; -- clojure.tools.logging.impl ---------------------------------------------------------------------------------------------

(def clojure-tools-logging-impl-ns-sym 'clojure.tools.logging.impl)

(defn try-resolve-clojure-tools-logging-impl-ns-symbol* [sym]
  (try
    (require clojure-tools-logging-impl-ns-sym)
    (ns-resolve clojure-tools-logging-impl-ns-sym sym)
    (catch Throwable e
      (throw (ex-info (str "clearcut: Unable to require " clojure-tools-logging-impl-ns-sym ".") {} e)))))

(def try-resolve-clojure-tools-logging-impl-ns-symbol (memoize try-resolve-clojure-tools-logging-impl-ns-symbol*))

(defn try-resolve-clojure-tools-logging-impl-var* [sym]
  (let [v (try-resolve-clojure-tools-logging-impl-ns-symbol sym)]
    (if (var? v)
      (var-get v)
      (throw (ex-info (str "clearcut: Unable to resolve var " sym " in " clojure-tools-logging-impl-ns-sym ".") {})))))

(def try-resolve-clojure-tools-logging-impl-var (memoize try-resolve-clojure-tools-logging-impl-var*))

(defn get-clojure-tools-logging-impl-get-logger []
  (try-resolve-clojure-tools-logging-impl-var 'get-logger))

(defn get-clojure-tools-logging-impl-enabled? []
  (try-resolve-clojure-tools-logging-impl-var 'enabled?))

; -- public -----------------------------------------------------------------------------------------------------------------

(defn log* [ns level throwable message]
  ; TODO: maybe resolve symbols at compile-time?
  ; TODO: maybe resolve clojure.tools.logging/log macro at compile time and use it for expansion?
  (if-let [log-fn (get-clojure-tools-logging-log*)]
    (if-let [logger-factory (get-clojure-tools-logging-logger-factory)]
      (if-let [get-logger (get-clojure-tools-logging-impl-get-logger)]
        (if-let [enabled? (get-clojure-tools-logging-impl-enabled?)]
          ; expansion from clojure.tools.logging/log macro
          (let [logger (get-logger logger-factory ns)]
            (if (enabled? logger level)
              (log-fn logger level throwable message))))))))

(defn control-item? [item]
  (or (instance? Style item)
      (instance? Format item)))

(defn filter-control-items [items]
  (remove control-item? items))

(defn print-items [items]
  ; TODO: make this pluggable?
  ; TODO: maybe implement formatting/styling via ANSI
  (let [filtered-items (filter-control-items items)]
    (apply print-str filtered-items)))

(defn log [ns level & items]
  (let [clojure-logging-level (helpers/level-to-clojure-logging-level level)
        first-item (first items)
        throwable (if (instance? Throwable first-item) first-item)
        message (print-items (if (some? throwable) (rest items) items))]
    (log* ns clojure-logging-level throwable message)))
