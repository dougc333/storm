(ns nom.nom.nom
  (:import [org.jdom.adapters CrimsonDOMAdapter])
  (:require [sample2.core])
  (:gen-class))

(when-not (= "1.1.0" (clojure-version))
  (throw (Exception. (str "Not running Clojure 1.1.0 Snapshot: "
                          (clojure-version)))))

(def unused-proxy (proxy [Object] [] (toString [] "unused")))

(defn -main [& args]
  (when-not (empty? args)
    (println "NOM! Munched" (first args))
    (recur (rest args))))
