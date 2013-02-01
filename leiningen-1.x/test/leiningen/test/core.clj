(ns leiningen.test.core
  (:use [clojure.test]
        [leiningen.core]
        [leiningen.test.helper :only [sample-project]]))

(deftest test-make-groups-empty-args
  (is (= [[]] (make-groups []))))

(deftest test-make-groups-single-task
  (is (= [["pom"]] (make-groups ["pom"]))))

(deftest test-make-groups-without-args
  (is (= [["clean"] ["deps"] ["test"]]
         (make-groups ["clean," "deps," "test"]))))

(deftest test-make-groups-with-args
  (is (= [["test" "test-core"] ["version"]]
         (make-groups ["test" "test-core," "version"]))))

(deftest test-make-groups-with-long-chain
  (is (= [["help" "help"] ["help" "version"] ["version"]
          ["test" "test-compile"]]
         (make-groups '("help" "help," "help" "version," "version,"
                        "test" "test-compile")))))

(deftest test-matching-arity-with-project
  (is (matching-arity? "test" {} []))
  (is (matching-arity? "test" {} ["test-core"]))
  (is (not (matching-arity? "version" {} ["bogus" "arg" "s"])))
  (is (matching-arity? "search" {} ["clojure"]))
  (is (matching-arity? "search" {} ["clojure" "2"])))

(deftest test-matching-arity-without-project
  (is (matching-arity? "version" nil []))
  (is (not (matching-arity? "test" nil [])))
  (is (not (matching-arity? "test" nil ["test-core"])))
  (is (matching-arity? "search" nil ["clojure"]))
  (is (matching-arity? "search" nil ["clojure" "2"])))

(deftest test-unquote
  (is (= ['org.clojure/clojure "1.1.0"]
           (first (:dependencies sample-project))))
  (is (= '(fn [_] (> (rand) 0.5)))))

(deftest test-version-greater-eq
  (is (version-greater-eq? "1.5.0" "1.4.2"))
  (is (not (version-greater-eq? "1.4.2" "1.5.0")))
  (is (version-greater-eq? "1.2.3" "1.1.1"))
  (is (version-greater-eq? "1.2.0" "1.2"))
  (is (version-greater-eq? "1.2" "1")))

(deftest test-repositories-for-omitting-central
  (let [repos (repositories-for
                {:omit-default-repositories true
                 :repositories {"repo-1" "http://repo-1-url"}})]
    (is (= ["http://disabled-central" "http://repo-1-url"]
           (map :url (vals repos))))))

(deftest test-repositories-for-including-defaults
  (let [repos (repositories-for sample-project)]
    (is (get repos "central"))
    (is (get repos "clojars"))
    (is (get repos "snapshots"))))

(deftest test-repositories-for-many-repos-ordered
  (let [repo-names (map #(str "repo-" %) (range 20))
        fake-url-ify #(str % "-url")
        repos (repositories-for
                {:omit-default-repositories true
                 :repositories (map #(vector % (fake-url-ify %))
                                    repo-names)})]
    (is (= clojure.lang.PersistentArrayMap (class repos)))
    (is (= {:url "repo-11-url"} (get repos "repo-11")))
    (is (= (map fake-url-ify repo-names) (rest (map :url (vals repos)))))))

