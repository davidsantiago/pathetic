(defproject pathetic "0.5.0-SNAPSHOT"
  :description "The missing path handling."
  :dependencies [[org.clojure/clojure "1.5.1"]]

  :source-paths ["generated-src"]
  :plugins [[com.keminglabs/cljx "0.2.2"]]
  :hooks [cljx.hooks]
  :cljx {:builds [{:source-paths ["src"]
                   :output-path "generated-src"
                   :rules cljx.rules/clj-rules}

                  {:source-paths ["src"]
                   :output-path "generated-src"
                   :extension "cljs"
                   :rules cljx.rules/cljs-rules}]}
  :profiles {:clojure1.3 {:dependencies [[org.clojure/clojure "1.3.0"]]}})
