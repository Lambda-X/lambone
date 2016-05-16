(ns <<project-ns>>.core
  "The main application namespace"
  (:require [mount.core :as mount]
            [clojure.tools.logging :as log]
            [clojure.tools.cli :as cli]
            [robert.hooke :as hooke]
            [<<project-ns>>.system :as system]
            [<<project-ns>>.logging :as logging])
  (:gen-class))

(def cli-options
  "Customize at will"
  [#_["-p" "--port PORT" "Port number"
      :parse-fn #(Integer/parseInt %)]])

(defn stop
  []
  (mount/stop)
  (logging/without-logging-status)
  (log/info "<=< Stopped"))

(defn start
  [args]
  (logging/with-logging-status)
  (-> args
      (cli/parse-opts cli-options)
      mount/start-with-args)
  (log/info ">=> Started" (system/greeting system/config)))

(defn main-stop
  "Hook for -main side-effects on stop.

  For instance this function should call shutdown-agents, which is not
  desirable when stopping the app at the repl."
  [f & args]
  (apply f args)
  (shutdown-agents))

(defn main-start
  "Hook for -main side-effects on start.

  For instance this function should set .addShutdownHook and perform
  all the side effects that need to be avoided when working at the
  repl."
  [f & args]
  (.addShutdownHook (Runtime/getRuntime) (Thread. stop))
  (apply f args))

(Thread/setDefaultUncaughtExceptionHandler
 (reify Thread$UncaughtExceptionHandler
   (uncaughtException [_ thread ex]
     (log/error ex "Uncaught exception on" (.getName thread)))))

(defn -main
  [& args]
  (hooke/add-hook #'<<project-ns>>.core/stop #'<<project-ns>>.core/main-stop)
  (hooke/add-hook #'<<project-ns>>.core/start #'<<project-ns>>.core/main-start)
  (start args))
