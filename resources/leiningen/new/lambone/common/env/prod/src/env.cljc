(ns <<project-ns>>.env
  (:require
   #?@(:clj [[taoensso.timbre :as timbre]])))

(def ^{:doc "The default clj/cljs environment"}
  defaults
  {:greeting "Project <<project-ns>>"
   ;; Log configuration:
   ;; for clj, see https://github.com/ptaoussanis/timbre#configuration
   ;; for cljs, see https://github.com/adzerk-oss/cljs-console
   :logging {:level :warn}})
