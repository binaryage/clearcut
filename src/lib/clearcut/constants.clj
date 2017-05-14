(ns clearcut.constants)

; we follow Clojure's clojure.tools.logging convention
; :trace, :debug, :info, :warn, :error, :fatal
(def ^:const level-fatal 1)
(def ^:const level-error 2)
(def ^:const level-warn 3)
(def ^:const level-info 4)
(def ^:const level-debug 5)
(def ^:const level-trace 6)

(def levels {:fatal level-fatal
             :error level-error
             :warn  level-warn
             :info  level-info
             :debug level-debug
             :trace level-trace})

(def all-levels (set (vals levels)))

; -- constants for runtime state slots --------------------------------------------------------------------------------------

(defmacro call-site-error-idx [] 0)
(defmacro console-reporter-idx [] 1)
(defmacro error-reported-idx [] 2)

; -- constants exported for cljs --------------------------------------------------------------------------------------------

(defmacro emit-level-fatal []
  level-fatal)

(defmacro emit-level-error []
  level-error)

(defmacro emit-level-warn []
  level-warn)

(defmacro emit-level-info []
  level-info)

(defmacro emit-level-debug []
  level-debug)

(defmacro emit-level-trace []
  level-trace)

(defmacro emit-all-levels []
  all-levels)
