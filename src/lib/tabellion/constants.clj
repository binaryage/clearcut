(ns tabellion.constants)

; we follow Clojure's clojure.tools.logging convention
; :trace, :debug, :info, :warn, :error, :fatal
(def ^:const level-silent 0)
(def ^:const level-fatal 1)
(def ^:const level-error 2)
(def ^:const level-warn 3)
(def ^:const level-info 4)
(def ^:const level-debug 5)
(def ^:const level-trace 6)

(def all-levels (set [0 1 2 3 4 5 6]))

; -- constants for runtime state slots --------------------------------------------------------------------------------------

(defmacro call-site-error-idx [] 0)
(defmacro console-reporter-idx [] 1)
(defmacro error-reported-idx [] 2)
