(ns <<project-ns>>.core
  "The main application namespace"
  (:require [mount.core :as mount]
            [clojure.tools.logging :as log]
            [clojure.tools.cli :as cli]
            [clojure.string :as string]
            [robert.hooke :as hooke]
            [<<project-ns>>.system :as system]
            [<<project-ns>>.logging :as logging])
  (:gen-class))

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
  (.addShutdownHook (Runtime/getRuntime) (Thread. #(system/stop)))
  (apply f args))

(defn exit [status msg]
  (.println *err* msg)
  (System/exit status))

(defn usage [options-summary]
  (println (str "Options:\n" options-summary)))

(defn error-msg [errors]
  (str "The following errors occurred while parsing your command:\n\n"
       (string/join "\n  " (cons "" errors))
       "\n"))

(def cli-options
  "Customize at will"
  [#_["-r" "--rollback" nil "Rollback database tables."
      :id :rollback
      :default false]
   #_["-m" "--migrate" nil "Migrate database tables."
      :id :migrate
      :default false]
   ["-h" "--help"]])

(defn -main
  [& args]
  (hooke/add-hook #'<<project-ns>>.system/stop #'<<project-ns>>.core/main-stop)
  (hooke/add-hook #'<<project-ns>>.system/start #'<<project-ns>>.core/main-start)
  (let [{:keys [options
                arguments
                errors
                summary] :as args} (cli/parse-opts args cli-options)]
    (cond
      (:help options) (exit 0 (usage summary))
      errors (exit 1 (error-msg errors))
      true (system/start args))))
