(ns leiningen.test!
  "Run the project's tests after cleaning and fetching dependencies."
  (:refer-clojure :exclude [test])
  (:use [leiningen.clean :only [clean]]
        [leiningen.deps :only [deps]]
        [leiningen.test :only [test]]))

(defn test!
  "Run a project's tests after cleaning and fetching dependencies."
  [project & nses]
  (apply test (doto project clean deps) nses))
