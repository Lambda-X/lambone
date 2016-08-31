(ns dev
  (:require boot
            [clojure.pprint :refer [pprint]]
            [clojure.test :as test]
            [clojure.reflect :refer [reflect]]
            [clojure.repl :refer [apropos dir doc find-doc pst source]]
            [clojure.tools.namespace.find :as tnfind]
            [clojure.java.classpath :as jcpath]
            [clojure.java.io :as io]
            [mount.core :as mount]
            [mount.tools.graph :as mount-graph]
            [clojure.tools.logging :as log :refer [spy spyf]]
            [adzerk.env :refer [env]]
            [<<project-ns>>.core :as core]
            [<<project-ns>>.system :as system]
            [<<project-ns>>.utils :as utils]))

(defn config
  "Pretty print the system status"
  []
  (pprint system/config))

(defn status
  "Pretty print the system status"
  []
  (pprint (mount-graph/states-with-deps)))

(def start #(system/start nil)) ;; no args to start

(def stop system/stop)

(defn go []
  (utils/safely (start)
                #(log/error % "Error in dev/go"))
  :ready)

(defn test-all []
  (let [test-namespaces (->> (jcpath/classpath-directories)
                             (tnfind/find-namespaces)
                             (filter #(re-find #"booma.*-test$" (str %))))]
    (run! require test-namespaces)
    (apply test/run-tests test-namespaces)))

;; Deps

(def ^{:doc "Load a dependency on the classpath.

  It expects a (quoted) dependency vector (e.g. '[cheshire \"5.6.1\"]). It then
  resolves the corresponding jar, downloading it in case, and adds it to the
  classpath, ready to use." } hotload boot/hotload!)
