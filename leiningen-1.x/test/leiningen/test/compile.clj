(ns leiningen.test.compile
  (:refer-clojure :exclude [compile])
  (:use [clojure.test]
        [clojure.java.io :only [file]]
        [clojure.java.shell :only [with-sh-dir]]
        [leiningen.compile]
        [leiningen.core :only [read-project]]
        [leiningen.test.helper :only [sample-project sample-failing-project
                                      tricky-name-project dev-deps-project]]
        [leiningen.util.file :only [delete-file-recursively]]))

(use-fixtures :each (fn [f]
                      (delete-file-recursively
                       (file "test_projects" "sample" "classes") true)
                      (delete-file-recursively
                       (file "test_projects" "sample_failing" "classes") true)
                      (f)))

(deftest test-compile
  (is (zero? (compile sample-project)))
  (is (.exists (file "test_projects" "sample"
                     "classes" "nom" "nom" "nom.class")))
  (is (pos? (compile sample-failing-project))))

(deftest test-compile-regex
  (is (zero? (compile (assoc tricky-name-project :aot [#"unch"]))))
  (is (not (.exists (file (:compile-path tricky-name-project)
                          "org" "domain" "tricky_name" "core__init.class"))))
  (is (.exists (file (:compile-path tricky-name-project)
                     "org" "domain" "tricky_name" "brunch__init.class")))
  (is (.exists (file (:compile-path tricky-name-project)
                     "org" "domain" "tricky_name" "munch__init.class"))))

(deftest test-plugin
  (is (zero? (eval-in-project (assoc sample-project
                                :eval-in-leiningen true
                                :skip-shutdown-agents true
                                :main nil)
                              '(do (require 'leiningen.compile)
                                   :compiled)))))

(deftest test-cleared-transitive-aot
  (is (zero? (compile (assoc sample-project
                        :clean-non-project-classes true))))
  (is (zero? (eval-in-project sample-project
                              '(require 'nom.nom.nom)))
      "can't load after compiling")
  (let [classes (seq (.list (file "test_projects" "sample"
                                  "classes" "nom" "nom")))]
    (doseq [r [#"nom\$fn__\d+.class" #"nom\$loading__\d+__auto____\d+.class"
               #"nom\$_main__\d+.class" #"nom.class" #"nom__init.class"]]
      (is (some (partial re-find r) classes) (format "missing %s" r))))
  (is (not (.exists (file "test_projects" "sample"
                          "classes" "sample2" "core.class"))))
  (is (not (.exists (file "test_projects" "sample"
                          "classes" "sample2" "alt.class")))))

(deftest test-cleared-transitive-aot-by-regexes
  (is (zero? (compile (assoc sample-project
                        :clean-non-project-classes [#"core"]))))
  (let [classes (seq (.list (file "test_projects" "sample"
                                  "classes" "nom" "nom")))]
    (doseq [r [#"nom\$fn__\d+.class" #"nom\$loading__\d+__auto____\d+.class"
               #"nom\$_main__\d+.class" #"nom.class" #"nom__init.class"]]
      (is (some (partial re-find r) classes) (format "missing %s" r))))
  (is (not (.exists (file "test_projects" "sample"
                          "classes" "sample2" "core.class"))))
  (is (.exists (file "test_projects" "sample" "classes"
                     "sample2" "alt__init.class"))))

(deftest test-skip-aot-on-main
  (delete-file-recursively (:compile-path tricky-name-project) :silent)
  (is (zero? (compile tricky-name-project)))
  (is (empty? (.list (file (:compile-path tricky-name-project))))))

(deftest test-injection
  (is (zero? (eval-in-project sample-project
                              '#'leiningen.util.injected/add-hook))))

(deftest test-compile-java-main
  (is (zero? (compile dev-deps-project))))

(deftest test-jvm-opts
  (is (= ["-Dhello=\"guten tag\"" "-XX:+HeapDumpOnOutOfMemoryError"]
         (get-jvm-opts-from-env (str "-Dhello=\"guten tag\" "
                                     "-XX:+HeapDumpOnOutOfMemoryError")))))