(ns tabellion.defaults
  "Default configuration + specs."
  (:require [cljs.env]
            [clojure.set]
            [tabellion.state]
            [tabellion.constants :as constants]
            [clojure.spec :as s]))

(def config                                                                                                                   ; falsy below means 'nil' or 'false'
  {; -- compiler config -----------------------------------------------------------------------------------------------------
   :diagnostics                                true                                                                           ; #{true falsy}
   :skip-config-validation                     false                                                                          ; #{true falsy}
   :macroexpand-selectors                      true                                                                           ; #{true falsy}
   :elided-log-levels                          #{4 5 6}                                                                       ; subsets of constants/all-levels

   ; compile-time warnings/errors

   ; -- runtime config ------------------------------------------------------------------------------------------------------

   ; run-time warnings/errors

   ; reporting modes
   :runtime-error-reporting                    :throw                                                                         ; #{:throw :console falsy}
   :runtime-warning-reporting                  :console                                                                       ; #{:throw :console falsy}

   :runtime-throw-errors-from-macro-call-sites true                                                                           ; #{true falsy}
   :runtime-use-envelope                       true                                                                           ; #{true falsy}

   ; -- development ---------------------------------------------------------------------------------------------------------
   ; enable debug if you want to debug/hack tabellion itself
   :debug                                      false                                                                          ; #{true falsy}
   })

(def advanced-mode-config-overrides
  {:diagnostics false})

; -- config validation specs ------------------------------------------------------------------------------------------------

; please note that we want this code to be co-located with default config for easier maintenance
; but formally we want config spec to reside in tabellion.config namespace
(alias 'config 'tabellion.config)

; -- config helpers ---------------------------------------------------------------------------------------------------------

(s/def ::config/boolish #(contains? #{true false nil} %))
(s/def ::config/message #(contains? #{:error :warn false nil} %))
(s/def ::config/reporting #(contains? #{:throw :console false nil} %))
(s/def ::config/log-level #(contains? constants/all-levels %))
(s/def ::config/log-levels-set #(clojure.set/subset? % constants/all-levels))

; -- config keys ------------------------------------------------------------------------------------------------------------

(s/def ::config/diagnostics ::config/boolish)
(s/def ::config/skip-config-validation ::config/boolish)
(s/def ::config/macroexpand-selectors ::config/boolish)
(s/def ::config/elided-log-levels ::config/log-levels-set)

(s/def ::config/runtime-error-reporting ::config/reporting)
(s/def ::config/runtime-warning-reporting ::config/reporting)

(s/def ::config/runtime-throw-errors-from-macro-call-sites ::config/boolish)
(s/def ::config/runtime-use-envelope ::config/boolish)

(s/def ::config/debug ::config/boolish)

; -- config map -------------------------------------------------------------------------------------------------------------

(s/def ::config/config
  (s/keys :req-un [::config/diagnostics
                   ::config/skip-config-validation
                   ::config/macroexpand-selectors
                   ::config/elided-log-levels
                   ::config/runtime-error-reporting
                   ::config/runtime-warning-reporting
                   ::config/runtime-throw-errors-from-macro-call-sites
                   ::config/runtime-use-envelope
                   ::config/debug]))
