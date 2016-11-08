(ns tabellion.messages
  "A subsystem for printing compile-time warnings and errors. Piggybacks on cljs.analyzer."
  (:require                                                                                                                   ;[cljs.analyzer :as ana]
    [tabellion.debug :refer [debug-assert]]))

; TODO: maybe this won't be needed after all

(def ^:dynamic *tabellion-message-prefix* "Tabellion")

; -- helpers ----------------------------------------------------------------------------------------------------------------

(def message-ids
  [:dynamic-selector-usage
   :static-nil-target-object
   :static-unexpected-empty-selector
   :static-unexpected-punching-selector
   :static-unexpected-soft-selector])

(defn messages-registered? [table]
  (debug-assert (map? table))
  (let [result (contains? table (first message-ids))]
    (debug-assert (or (not result) (every? #(contains? table %) (rest message-ids))))
    result))

(defn register-messages [table]
  (debug-assert (map? table))
  (merge table (zipmap message-ids (repeat (count message-ids) true))))

(defn ^:dynamic post-process-message [msg]
  (str *tabellion-message-prefix* ", " msg))

(defmacro gen-tabellion-message-prefix []
  *tabellion-message-prefix*)

; -- compile-time error/warning messages (in hooked cljs compiler) ----------------------------------------------------------

;(defmethod ana/error-message :dynamic-selector-usage [type info]
;  (debug-assert (some #{type} message-ids))
;  (let [command (first (:form info))]
;    (post-process-message (str "Unexpected dynamic selector usage"
;                               (if (static-macro? command)
;                                 (str " (consider using " command "+)"))))))
;

; WARNING: when adding a new method here, don't forget to update register-messages as well
