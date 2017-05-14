(ns clearcut.core
  "Public macros to be consumed via core.cljs."
  (:refer-clojure :exclude [format])
  (:require [clojure.spec.alpha :as s]
            [clearcut.constants :as constants]
            [clearcut.types]
            [clearcut.codegen :refer [gen-log]]
            [clearcut.compiler :refer [with-compiler-context!]])
  (:import (clearcut.types Format Style)))

(defn gen-log-with-env [form env level items]
  (with-compiler-context! form env
    (gen-log level items)))

(defmacro style [css]
  `(clearcut.types/->Style ~css))

(defmacro format [fmtstr]
  `(clearcut.types/->Format ~fmtstr))

; -- core macros ------------------------------------------------------------------------------------------------------------

(defmacro gen-variadic-invoke [level-key items]
  (gen-log-with-env &form &env (level-key constants/levels) items))

(defmacro fatal [& items]
  (gen-log-with-env &form &env constants/level-fatal items))

(defmacro error [& items]
  (gen-log-with-env &form &env constants/level-error items))

(defmacro warn [& items]
  (gen-log-with-env &form &env constants/level-warn items))

(defmacro info [& items]
  (gen-log-with-env &form &env constants/level-info items))

(defmacro debug [& items]
  (gen-log-with-env &form &env constants/level-debug items))

(defmacro trace [& items]
  (gen-log-with-env &form &env constants/level-trace items))

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
