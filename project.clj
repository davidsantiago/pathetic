(defproject pathetic "0.5.1-SNAPSHOT"
  :description "The missing path handling."
  :dependencies [[org.clojure/clojure "1.5.1"]]

  :source-paths ["target/generated-src"]
  :test-paths ["target/generated-test"]
  :plugins [[com.keminglabs/cljx "0.3.0"]
            [lein-cljsbuild "0.3.2"]]
  :hooks [cljx.hooks]
  :cljx {:builds [{:source-paths ["src"]
                   :output-path "target/generated-src"
                   :rules :clj}

                  {:source-paths ["src"]
                   :output-path "target/generated-src"
                   :rules :cljs}

                  {:source-paths ["test"]
                   :output-path "target/generated-test"
                   :rules :clj}

                  {:source-paths ["test"]
                   :output-path "target/generated-test"
                   :rules :cljs}]}
  :profiles {:clojure1.4 {:dependencies [[org.clojure/clojure "1.4.0"]]}
             :clojure1.5 {:dependencies [[org.clojure/clojure "1.5.1"]]}
             :dev {:dependencies [[com.cemerick/clojurescript.test "0.0.4"]]}}
  :cljsbuild {:builds [{:source-paths ["target/generated-src"
                                       "target/generated-test"]
                        :compiler {:output-to "target/cljs/testable.js"
                        :optimizations :whitespace
                        :pretty-print true}}]
              :test-commands {"unit-tests" ["runners/phantomjs.js"
                                            "target/cljs/testable.js"]}})
