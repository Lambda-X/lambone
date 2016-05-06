(ns <<project-ns>>.logging
  {:from 'tolitius/mount
   :doc "For the configuration:
   - for clj, see https://logging.apache.org/log4j/2.x/manual/configuration.html
   - for cljs, see https://github.com/adzerk-oss/cljs-console"}
  (:require [mount.core :as mount]
            [robert.hooke :as hooke]
            [clojure.string :as string]
            [clojure.tools.logging :as log]))

(alter-meta! *ns* assoc ::load false)

(defn- f-to-action [f {:keys [status]}]
  (let [fname (-> (str f)
                  (string/split #"@")
                  first)]
    (case fname
      "mount.core$up" (when-not (contains? status :started) :up)
      "mount.core$down" (when-not (contains? status :stopped) :down)
      :noop)))

(defn whatcha-doing? [action]
  (case action
    :up ">=> starting"
    :down "<=< stopping"
    nil))

(defn log-status [f & args]
  (let [[state-name state] args
        action (f-to-action f state)]
    (when-let [taking-over-the-world (whatcha-doing? action)]
      (log/debug taking-over-the-world state-name))
    (apply f args)))

(defonce lifecycle-fns
  #{#'mount.core/up #'mount.core/down})

(defn with-logging-status []
  (doall (map #(hooke/add-hook % log-status) lifecycle-fns)))

(defn without-logging-status []
  (doall (map #(hooke/clear-hooks %) lifecycle-fns)))
