(ns <<project-ns>>.app
  (:require [adzerk.cljs-console :as log :include-macros true]))

(enable-console-print!)

(defn init []
  (log/info "Welcome to <<name|title>>!"))
