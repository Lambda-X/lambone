(ns <<project-ns>>.app
  (:require [adzerk.cljs-console :as log :include-macros true]
            [<<project-ns>>.env :as env]
            [<<project-ns>>.io :as io]))

(enable-console-print!)

(defn init []
  (io/print-version! (:version-path env/defaults))
  (log/info "Welcome to ~(:greeting env/defaults)"))
