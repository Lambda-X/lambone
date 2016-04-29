(ns edge.main
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require [cognitect.transit :as t]
            [om.next :as om :refer-macros [defui]]
            [om.dom :as dom]
            [goog.dom :as gdom]
            [cljs.core.async :as a :refer [chan >! <! close! timeout alts! mult tap mix pub sub]]
            [adzerk.cljs-console :as log :include-macros true]))

(defn init []
  (enable-console-print!)
  (log/warn "Congratulations - your environment seems to be working"))
