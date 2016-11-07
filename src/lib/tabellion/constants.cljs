(ns tabellion.constants
  (:require-macros [tabellion.constants :as c]))

(def level-silent (c/emit-level-silent))
(def level-fatal (c/emit-level-fatal))
(def level-error (c/emit-level-error))
(def level-warn (c/emit-level-warn))
(def level-info (c/emit-level-info))
(def level-debug (c/emit-level-debug))
(def level-trace (c/emit-level-trace))

(def all-levels (c/emit-all-levels))
