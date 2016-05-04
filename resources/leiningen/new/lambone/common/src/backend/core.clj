(ns <<name>>.core
  "The main application namespace"
  (:require [mount.core :as mount]
            [taoensso.timbre :as log]
            [clojure.tools.cli :as cli]
            [<<name>>.system :as system]
            [<<name>>.logging :as logging])
  (:gen-class))

(def cli-options
  "Customize at will"
  [#_["-p" "--port PORT" "Port number"
      :parse-fn #(Integer/parseInt %)]])

(defn stop
  []
  (mount/stop)
  (log/info "[Stopped]")
  (shutdown-agents))

(defn start
  [args]
  (-> args
      (cli/parse-opts cli-options)
      mount/start-with-args)
  (log/info "[Started]" (system/greeting system/config))
  (.addShutdownHook (Runtime/getRuntime) (Thread. stop)))

(defn -main [& args]
  (start args))
