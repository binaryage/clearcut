(ns tabellion.messages
  "A subsystem for printing runtime warnings and errors."
  (:require-macros [tabellion.messages :refer [gen-tabellion-message-prefix]]))

; -- helpers ----------------------------------------------------------------------------------------------------------------

(defn ^:dynamic post-process-message [msg]
  (str (gen-tabellion-message-prefix) ", " msg))

; -- runtime error/warning messages -----------------------------------------------------------------------------------------

(defmulti runtime-message (fn [type & _] type))

;(defmethod runtime-message :unexpected-soft-selector [_type]
;  (post-process-message (str "Unexpected soft selector (\"?\" does not make sense with oset!)")))
