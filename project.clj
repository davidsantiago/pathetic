(defproject pathetic "0.5.0-SNAPSHOT"
  :description "The missing path handling."
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [com.cemerick/clojurescript.test "0.0.4"]]

  :source-paths ["target/generated-src"]
  :test-paths ["target/generated-test"]
  :plugins [[com.keminglabs/cljx "0.3.0"]
            [lein-cljsbuild "0.3.2"]]
  :hooks [cljx.hook leiningen.cljsbuild]
  :cljx {:builds [{:source-paths ["src"]
                   :output-path "target/generated-src"
                   :rules cljx.rules/clj-rules}

                  {:source-paths ["src"]
                   :output-path "target/generated-src"
                   :extension "cljs"
                   :rules cljx.rules/cljs-rules}

                  {:source-paths ["test"]
                   :output-path "target/generated-test"
                   :rules cljx.rules/clj-rules}

                  {:source-paths ["test"]
                   :output-path "target/generated-test"
                   :extension "cljs"
                   :rules cljx.rules/cljs-rules}]}
  :profiles {:clojure1.2 {:dependencies [[org.clojure/clojure "1.2.1"]]}
             :clojure1.3 {:dependencies [[org.clojure/clojure "1.3.0"]]}
             :clojure1.4 {:dependencies [[org.clojure/clojure "1.4.0"]]}
             :clojure1.5 {:dependencies [[org.clojure/clojure "1.5.1"]]}}
  :cljsbuild {:builds [{:source-paths ["target/generated-src" "target/generated-test"]
                        :compiler {:output-to "target/cljs/testable.js"
                        :optimizations :whitespace
                        :pretty-print true}}]
              :test-commands {"unit-tests" ["runners/phantomjs.js"
                                            "target/cljs/testable.js"]}})
