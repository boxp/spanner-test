(defproject spanner-test "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/core.async "0.2.395"]
                 [com.stuartsierra/component "0.3.2"]
                 [com.google.cloud/google-cloud-spanner "0.17.2-beta"]
                 [org.clojure/tools.namespace "0.2.10"]
                 [environ "1.1.0"]]
  :profiles
  {:dev {:source-paths ["src" "dev"]}
   :uberjar {:main spanner-test.system}})
