(ns tabellion.core
  "Public macros to be consumed via core.cljs."
  (:require [clojure.spec :as s]
            [tabellion.constants :as constants]
            [tabellion.codegen :refer [gen-log]]
            [tabellion.compiler :refer [with-compiler-context!]]))

; -- core macros ------------------------------------------------------------------------------------------------------------

(defmacro fatal [& items]
  (with-compiler-context! &form &env
    (gen-log constants/level-fatal items)))

(defmacro error [& items]
  (with-compiler-context! &form &env
    (gen-log constants/level-error items)))

(defmacro warn [& items]
  (with-compiler-context! &form &env
    (gen-log constants/level-warn items)))

(defmacro info [& items]
  (with-compiler-context! &form &env
    (gen-log constants/level-info items)))

(defmacro debug [& items]
  (with-compiler-context! &form &env
    (gen-log constants/level-debug items)))

(defmacro trace [& items]
  (with-compiler-context! &form &env
    (gen-log constants/level-trace items)))

; -- specs for our macro apis -----------------------------------------------------------------------------------------------
;
; This is not much useful because we cannot reason about macro args much,
; but I include it because it is catching some edge cases
; and there is a room for possible further refinements.
; Additionally we do ad-hoc validations inside our macros.

(defn anything? [_] true)                                                                                                     ; TODO: use any? when we drop Clojure 1.8 support

(defmacro api-spec [args]
  `(s/fspec :args (s/cat ~@(if (symbol? args)
                             (var-get (resolve args))
                             args))
            :ret nil?))

(def log-args [:args (s/* anything?)])
(def log-api (api-spec log-args))

(s/def fatal log-api)
(s/def error log-api)
(s/def warn log-api)
(s/def info log-api)
(s/def debug log-api)
(s/def trace log-api)
