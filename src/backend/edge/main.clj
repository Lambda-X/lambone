(ns edge.main
  "The Main entry point"
  (:require [com.stuartsierra.component :as component]
            [taoensso.timbre :as log]
            [edge.system :as sys])
  (:gen-class))

(defn stop-app []
  (doseq [component (component/stop)]
    (log/info component "stopped"))
  (shutdown-agents))

(defn start-app [args]
  (doseq [component (-> (sys/new-production-system)
                        component/start)]
    (log/info component "started"))
  (.addShutdownHook (Runtime/getRuntime) (Thread. stop-app)))

(defn -main [& args]
  (start-app args))
