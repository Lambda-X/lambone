(ns <<project-ns>>.app
  (:require [cognitect.transit :as t]
            [goog.dom :as gdom]
            [reagent.core :as reagent :refer [atom]]
            [reagent.session :as session]
            [accountant.core :as accountant]
            [secretary.core :as secretary :include-macros true]
            [adzerk.cljs-console :as log :include-macros true]))

(enable-console-print!)

(defn init []
  (log/info "Welcome to test-seven!"))
