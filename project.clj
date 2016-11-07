(defproject binaryage/tabellion "0.1.0-SNAPSHOT"
  :description "Unified logging overlay on top of console.log and clojure.tools.logging."
  :url "https://github.com/binaryage/tabellion"
  :license {:name         "MIT License"
            :url          "http://opensource.org/licenses/MIT"
            :distribution :repo}

  :scm {:name "git"
        :url  "https://github.com/binaryage/tabellion"}

  :dependencies [[org.clojure/clojure "1.9.0-alpha14" :scope "provided"]
                 [org.clojure/clojurescript "1.9.293" :scope "provided"]
                 [org.clojure/tools.logging "0.3.1" :scope "provided"]
                 [binaryage/env-config "0.1.1"]
                 [funcool/cuerdas "2.0.0"]
                 [environ "1.1.0"]

                 [binaryage/devtools "0.8.2" :scope "test"]
                 [figwheel "0.5.8" :scope "test"]
                 [clj-logging-config "1.9.12" :scope "test"]
                 [clansi "1.0.0" :scope "test"]]

  :clean-targets ^{:protect false} ["target"
                                    "test/resources/.compiled"]

  :plugins [[lein-cljsbuild "1.1.4"]
            [lein-shell "0.5.0"]
            [lein-figwheel "0.5.8"]]

  ; this is just for IntelliJ + Cursive to play well
  :source-paths ["src/lib"]
  :test-paths ["test/src"]
  :resource-paths ^:replace ["test/resources"
                             "scripts"]

  :cljsbuild {:builds {}}                                                                                                     ; prevent https://github.com/emezeske/lein-cljsbuild/issues/413

  :profiles {:nuke-aliases
             {:aliases ^:replace {}}

             :lib
             ^{:pom-scope :provided}                                                                                          ; ! to overcome default jar/pom behaviour, our :dependencies replacement would be ignored for some reason
             [:nuke-aliases
              {:dependencies   ~(let [project-str (slurp "project.clj")
                                      project (->> project-str read-string (drop 3) (apply hash-map))
                                      test-dep? #(->> % (drop 2) (apply hash-map) :scope (= "test"))
                                      non-test-deps (remove test-dep? (:dependencies project))]
                                  (with-meta (vec non-test-deps) {:replace true}))                                            ; so ugly!
               :source-paths   ^:replace ["src/lib"]
               :resource-paths ^:replace []
               :test-paths     ^:replace []}]

             :clojure18
             {:dependencies [[org.clojure/clojure "1.8.0" :scope "provided"]
                             [clojure-future-spec "1.9.0-alpha13" :scope "provided"]]}

             :cooper
             {:plugins [[lein-cooper "1.2.2"]]}

             :figwheel
             {:figwheel {:server-port          7118
                         :server-logfile       ".figwheel/log.txt"
                         :validate-interactive false
                         :repl                 false}}

             :circus
             {:source-paths ["src/lib"
                             "test/src/circus"
                             "test/src/arena"
                             "test/src/tools"]}

             :testing-basic-onone
             {:cljsbuild {:builds {:basic-onone
                                   {:source-paths ["src/lib"
                                                   "test/src/runner"
                                                   "test/src/tools"
                                                   "test/src/tests"]
                                    :compiler     {:output-to       "test/resources/.compiled/basic_onone/main.js"
                                                   :output-dir      "test/resources/.compiled/basic_onone"
                                                   :asset-path      ".compiled/basic_onone"
                                                   :main            tabellion.runner
                                                   :optimizations   :none
                                                   :external-config {:devtools/config  {:dont-detect-custom-formatters true}
                                                                     :tabellion/config {:debug true}}}
                                    :figwheel     true}}}}
             :testing-basic-oadvanced
             {:cljsbuild {:builds {:basic-oadvanced
                                   {:source-paths ["src/lib"
                                                   "test/src/runner"
                                                   "test/src/tools"
                                                   "test/src/tests"]
                                    :compiler     {:output-to       "test/resources/.compiled/basic_oadvanced/main.js"
                                                   :output-dir      "test/resources/.compiled/basic_oadvanced"
                                                   :asset-path      ".compiled/basic_oadvanced"
                                                   :main            tabellion.runner
                                                   :pseudo-names    true
                                                   :optimizations   :advanced
                                                   :external-config {:tabellion/config {:debug true}}}}}}}
             :auto-testing
             {:cljsbuild {:builds {:basic-onone     {:notify-command ["scripts/rerun-tests.sh" "basic_onone"]}
                                   :basic-oadvanced {:notify-command ["scripts/rerun-tests.sh" "basic_oadvanced"]}}}}


             :dev-basic-onone
             {:cooper {"server"     ["scripts/launch-fixtures-server.sh"]
                       "figwheel"   ["lein" "fig-basic-onone"]
                       "repl-agent" ["scripts/launch-repl-with-agent.sh"]
                       "browser"    ["scripts/launch-test-browser.sh"]}}}

  :aliases {"test"                 ["do"
                                    ["clean"]
                                    ["shell" "scripts/run-tests.sh"]]
            "test-all"             ["shell" "scripts/run-all-tests.sh"]
            "dev-functional-tests" ["shell" "scripts/dev-functional-tests.sh"]
            "run-functional-tests" ["do"
                                    ["clean"]
                                    ["shell" "scripts/run-functional-tests.sh"]]
            "run-circus-tests"     ["do"
                                    ["clean"]
                                    ["shell" "scripts/run-circus-tests.sh"]]
            "build-tests"          ["do"
                                    ["with-profile" "+testing-basic-onone" "cljsbuild" "once" "basic-onone"]
                                    ["with-profile" "+testing-basic-oadvanced" "cljsbuild" "once" "basic-oadvanced"]
                                    ["with-profile" "+testing-basic-oadvanced" "cljsbuild" "once" "basic-oadvanced"]]
            "auto-build-tests"     ["do"
                                    ["with-profile" "+testing-basic-onone,+auto-testing" "cljsbuild" "once" "basic-onone"]
                                    ["with-profile" "+testing-basic-oadvanced" "cljsbuild" "once" "basic-oadvanced"]
                                    ["with-profile" "+testing-basic-oadvanced" "cljsbuild" "once" "basic-oadvanced"]]
            "fig-basic-onone"      ["with-profile" "+testing-basic-onone,+figwheel" "figwheel"]
            "auto-basic-onone"     ["with-profile" "+testing-basic-onone,+auto-testing" "cljsbuild" "auto" "basic-onone"]
            "auto-test"            ["do"
                                    ["clean"]
                                    ["auto-build-tests"]]
            "toc"                  ["shell" "scripts/generate-toc.sh"]
            "install"              ["do"
                                    ["shell" "scripts/prepare-jar.sh"]
                                    ["shell" "scripts/local-install.sh"]]
            "jar"                  ["shell" "scripts/prepare-jar.sh"]
            "deploy"               ["shell" "scripts/deploy-clojars.sh"]
            "release"              ["do"
                                    ["clean"]
                                    ["shell" "scripts/check-versions.sh"]
                                    ["shell" "scripts/prepare-jar.sh"]
                                    ["shell" "scripts/check-release.sh"]
                                    ["shell" "scripts/deploy-clojars.sh"]]})
