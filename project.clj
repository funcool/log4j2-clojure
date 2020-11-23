(defproject funcool/log4j2-clojure "2020.11.23-1"
  :description "Log4j2 clojure appender"
  :url "https://github.com/funcool/log4j2-clojure"
  :license {:name "MPL2.0"
            :url "http://mozilla.org/MPL/2.0/"}
  :javac-options ["-target" "11" "-source" "11" "-Xlint:-options" "-Xlint:deprecation"]
  :source-paths ["src"]
  :java-source-paths ["src"]
  :jar-name "log4j2-clojure.jar"
  :dependencies [[org.apache.logging.log4j/log4j-api "2.14.0"]
                 [org.apache.logging.log4j/log4j-core "2.14.0"]
                 [org.clojure/clojure "1.10.1"]])
