(defproject pathetic "0.5.0-SNAPSHOT"
  :description "The missing path handling."
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [com.cemerick/clojurescript.test "0.0.4"]]

  :source-paths ["generated-src"]
  :test-paths ["generated-test"]
  :plugins [[com.keminglabs/cljx "0.2.2"]
            [lein-cljsbuild "0.3.2"]]
;;  :hooks [cljx.hooks leiningen.cljsbuild] disabled due to a bug in cljx
  :cljx {:builds [{:source-paths ["src"]
                   :output-path "generated-src"
                   :rules cljx.rules/clj-rules}

                  {:source-paths ["src"]
                   :output-path "generated-src"
                   :extension "cljs"
                   :rules cljx.rules/cljs-rules}

                  {:source-paths ["test"]
                   :output-path "generated-test"
                   :rules cljx.rules/clj-rules}

                  {:source-paths ["test"]
                   :output-path "generated-test"
                   :extension "cljs"
                   :rules cljx.rules/cljs-rules}]}
  :cljsbuild {:builds [{:source-paths ["generated-src" "generated-test"]
                        :compiler {:output-to "target/cljs/testable.js"
                        :optimizations :whitespace
                        :pretty-print true}}]
              :test-commands {"unit-tests" ["runners/phantomjs.js" "target/cljs/testable.js"]}}
  :profiles {:clojure1.3 {:dependencies [[org.clojure/clojure "1.3.0"]]}})
