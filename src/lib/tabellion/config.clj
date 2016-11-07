(ns tabellion.config
  "Code supporting static (compile-time) configuration. See defaults.clj for config map."
  (:refer-clojure :exclude [gensym])
  (:require [cljs.env]
            [clojure.spec :as s]
            [env-config.core :as env-config]
            [tabellion.state]
            [tabellion.helpers :as helpers :refer [gensym]]
            [tabellion.defaults :as defaults]))

; this is for testing, see with-compiler-config macro below
(def adhoc-config-overrides (volatile! {}))

(def ^:dynamic env-config-prefix "tabellion")

; -- helpers ----------------------------------------------------------------------------------------------------------------

(defn get-adhoc-config-overrides []
  @adhoc-config-overrides)

(defn advanced-mode? []
  (if cljs.env/*compiler*
    (= (get-in @cljs.env/*compiler* [:options :optimizations]) :advanced)))

(defn prepare-default-config []
  (merge defaults/config (if (advanced-mode?) defaults/advanced-mode-config-overrides)))

(defn read-project-config []
  (if cljs.env/*compiler*
    (get-in @cljs.env/*compiler* [:options :external-config :tabellion/config])))                                                  ; https://github.com/bhauman/lein-figwheel/commit/80f7306bf5e6bd1330287a6f3cc259ff645d899b

(defn get-env-vars []
  (-> {}
      (into (System/getenv))
      (into (System/getProperties))))

(defn read-env-config []
  (env-config/make-config-with-logging env-config-prefix (get-env-vars)))

(def memoized-read-env-config (memoize read-env-config))

(def last-printed-config-explanation-str (volatile! nil))

(defn print-invalid-config-warning [explanation-str config-str]
  (let [indent (count "WARNING: ")]
    (println "WARNING: Detected problems in tabellion config:")
    (println (helpers/indent-text explanation-str (+ 2 (count "WARNING: "))))
    (println (helpers/indent-text (with-out-str
                                    (println "When validating:")
                                    (println (helpers/indent-text config-str 2))) indent))
    (println)))

(defn validate-config-and-report-problems-if-needed! [config]
  (if-not (:skip-config-validation config)
    (if-not (s/valid? ::config config)
      (let [explanation-str (s/explain-str ::config config)]
        (when-not (= @last-printed-config-explanation-str explanation-str)
          (vreset! last-printed-config-explanation-str explanation-str)
          (binding [*out* *err*]
            (print-invalid-config-warning explanation-str (helpers/pprint-code-str config))))))))

(defn ^:dynamic get-compiler-config []
  (let [config (merge (prepare-default-config)                                                                                ; must not be memoized! touches cljs.env/*compiler*
                      (read-project-config)                                                                                   ; must not be memoized! touches cljs.env/*compiler*
                      (memoized-read-env-config)
                      (get-adhoc-config-overrides))]
    (validate-config-and-report-problems-if-needed! config)
    config))

; -- compiler config access -------------------------------------------------------------------------------------------------

(defn get-current-compiler-config []
  (get-compiler-config))                                                                                                      ; TODO: should we somehow cache this?, the problem is that (read-project-config) might change between calls

(defn get-config-key [key & [config]]
  (key (or config (get-current-compiler-config))))

(defn get-runtime-config [& [config]]
  (let [* (fn [[key val]]
            (if-let [m (re-matches #"^runtime-(.*)$" (name key))]
              [(keyword (second m)) val]))]
    ; select all :runtime-something keys and drop :runtime- prefix
    (into {} (keep * (or config (get-current-compiler-config))))))

; -- updates to compiler config during compilation --------------------------------------------------------------------------

(def adhoc-config-overrides-stack (volatile! []))

(defmacro push-config-overrides! [config]
  (let [current-adhoc-config-overrides @adhoc-config-overrides]
    (vreset! adhoc-config-overrides-stack (conj @adhoc-config-overrides-stack current-adhoc-config-overrides))
    (vreset! adhoc-config-overrides (merge current-adhoc-config-overrides config))
    nil))

(defmacro pop-config-overrides! []
  (let [stack @adhoc-config-overrides-stack]
    (assert (pos? (count stack)))
    (vreset! adhoc-config-overrides (last stack))
    (vreset! adhoc-config-overrides-stack (butlast stack))
    nil))

; note: we rely on macroexpand behaviour here, it will expand
; push-config-overrides!first, then macros in body and then pop-config-overrides!
; this way we can tweak compiler config for an isolated code piece, which is handy for testing or ad-hoc config tweaks
(defmacro with-compiler-config [config & body]
  (let [result-sym (gensym "result")]
    `(do
       (tabellion.config/push-config-overrides! ~config)
       (let [~result-sym (do ~@body)]
         (tabellion.config/pop-config-overrides!)
         ~result-sym))))

(defmacro without-diagnostics [& body]
  `(tabellion.config/with-compiler-config {:diagnostics false}
     ~@body))

(defmacro with-debug [& body]
  `(tabellion.config/with-compiler-config {:debug true}
     ~@body))

; -- runtime macros ---------------------------------------------------------------------------------------------------------

(defmacro with-runtime-config [config & body]
  `(binding [tabellion.config/*runtime-config* (merge (tabellion.config/get-current-runtime-config) ~config)]
     ~@body))

(defmacro gen-runtime-config [& [config]]
  (tabellion.config/get-runtime-config config))

; -- icing ------------------------------------------------------------------------------------------------------------------

(defn diagnostics? [& [config]]
  (true? (get-config-key :diagnostics config)))

(defn debug? [& [config]]
  (true? (get-config-key :debug config)))

(defn macroexpand-selectors? [& [config]]
  (true? (get-config-key :macroexpand-selectors config)))
