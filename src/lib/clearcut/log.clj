(ns clearcut.log
  "Logging for debugging purposes."
  (:require [clojure.pprint]
            [clojure.string :as string]))

(defn trimr-nl [s]
  (string/replace s #"\n$" ""))

(defn pprint [v]
  (if (= v "\n")
    v
    (trimr-nl (with-out-str
                (binding [clojure.pprint/*print-right-margin* 200
                          clojure.core/*print-length* 10
                          clojure.core/*print-level* 5]
                  (clojure.pprint/pprint v))))))

(defn log [& args]
  (let [msg (apply str (interpose " " (map pprint args)))]
    (.print System/out (str msg "\n"))))
