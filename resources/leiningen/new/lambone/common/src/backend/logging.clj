(ns <<name>>.logging
  {:from 'tolitius/mount}
  (:require [mount.core :as mount]
            [robert.hooke :as hooke]
            [clojure.string :as string]
            [taoensso.timbre :as timbre]
            [<<name>>.system :as system]))

(alter-meta! *ns* assoc ::load false)

(defn- f-to-action [f]
  (let [fname (-> (str f)
                  (string/split #"@")
                  first)]
    (case fname
      "mount.core$up" :up
      "mount.core$down" :down
      :noop)))

(defn whatcha-doing? [{:keys [started? suspended? suspend]} action]
  (case action
    :up (if suspended? ">> resuming"
            (if-not started? ">> starting"))
    :down (if (or started? suspended?) "<< stopping")
    :suspend (if (and started? suspend) "<< suspending")
    :resume (if suspended? ">> resuming")))

(defn log-status [f & args]
  (let [{:keys [ns name] :as state} (second args)
        action (f-to-action f)]
    (when-let [taking-over-the-world (whatcha-doing? state action)]
      (timbre/info (str taking-over-the-world "..  " (ns-resolve ns name))))
    (apply f args)))

(defonce lifecycle-fns
  #{#'mount.core/up
    #'mount.core/down})

(defn with-logging-status []
  (doall (map #(hooke/add-hook % log-status) lifecycle-fns)))

(defn without-logging-status []
  (doall (map #(hooke/clear-hooks %) lifecycle-fns)))

(mount/defstate
  logging
  :start #(do (timbre/set-config! (:logging system/config))
              (with-logging-status))
  :stop (without-logging-status))
