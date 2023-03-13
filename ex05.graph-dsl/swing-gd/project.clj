(defproject swing-gd "0.1.0-SNAPSHOT"
  :description "Solution to exercise 5 - graphic DSL, using Clojure and Java Swing"
  :url "http://example.com/FIXME"
  :license {:name "MIT Licence"}
  :dependencies [[org.clojure/clojure "1.11.1"]]
  :main ^:skip-aot swing-gd.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})
