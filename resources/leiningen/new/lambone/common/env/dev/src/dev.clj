(ns dev
  {:from :juxt/edge}
  (:require [clojure.pprint :refer [pprint]]
            [clojure.test :refer [run-all-tests]]
            [clojure.reflect :refer [reflect]]
            [clojure.repl :refer [apropos dir doc find-doc pst source]]
            [clojure.tools.namespace.repl :refer [refresh refresh-all]]
            [clojure.java.io :as io]
            [mount.core :as mount :refer [start-without start-with start-with-states stop-except
                                          only except swap swap-states with-args]]
            [mount.tools.graph :as mount-graph]
            [schema.core :as schema]
            [taoensso.timbre :as timbre :refer [log trace debug info warn error fatal report
                                                logf tracef debugf infof warnf errorf fatalf reportf
                                                spy get-env log-env]]
            [taoensso.timbre.profiling :as profiling :refer [pspy pspy* profile defnp p p*]]
            [<<name>>.core :as core]
            [<<name>>.system :as system]))

(defn config
  "Pretty print the system status"
  []
  (pprint system/config))

(defn status
  "Pretty print the system status"
  []
  (pprint (mount-graph/states-with-deps)))

(def start mount/start)

(def stop mount/stop)

(defn go []
  (mount/start)
  :ready)

(defn reset []
  (mount/stop)
  (refresh :after 'dev/go))

(defn check
  "Check for component validation errors"
  [system]
  (let [errors
        (->> system
             (reduce-kv
              (fn [acc k v]
                (assoc acc k (schema/check (type v) v)))
              {})
             (filter (comp some? second)))]
    (when (seq errors) (into {} errors))))

(defn test-all []
  (run-all-tests #"<<name>>.*test$"))

(defn reset-and-test []
  (reset)
  (time (test-all)))
