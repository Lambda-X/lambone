(ns dev
  (:require [clojure.pprint :refer [pprint]]
            [clojure.test :refer [run-all-tests]]
            [clojure.reflect :refer [reflect]]
            [clojure.repl :refer [apropos dir doc find-doc pst source]]
            [clojure.tools.namespace.repl :as tnr]
            [clojure.java.io :as io]
            [mount.core :as mount :refer [start-without start-with start-with-states
                                          stop-except only except swap swap-states with-args]]
            [mount.tools.graph :as mount-graph]
            [clojure.tools.logging :as log :refer [spy spyf]]
            [adzerk.env :refer [env]]
            [<<project-ns>>.core :as core]
            [<<project-ns>>.system :as system]))

(defn config
  "Pretty print the system status"
  []
  (pprint system/config))

(defn status
  "Pretty print the system status"
  []
  (pprint (mount-graph/states-with-deps)))

(def start #(core/start nil)) ;; no args to start

(def stop core/stop)

(defn go []
  (start)
  :ready)

(defn reset []
  (stop)
  (tnr/refresh :after 'dev/go))

(defn refresh
  []
  (stop)
  (tnr/refresh))

(defn refresh-all
  []
  (stop)
  (tnr/refresh-all))

(defn test-all []
  (run-all-tests #"<<name>>.*test$"))

(defn reset-and-test []
  (reset)
  (time (test-all)))
