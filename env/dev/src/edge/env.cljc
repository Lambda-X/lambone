(ns edge.env
  (:require [taoensso.timbre :as timbre]))

(def defaults
  {:greeting "Project <<project-ns>> (DEV)"
   :logging {:level :debug  ; e/o #{:trace :debug :info :warn :error :fatal :report}

             ;; Control log filtering by namespaces/patterns. Useful for turning off
             ;; logging in noisy libraries, etc.:
             :ns-whitelist  [] #_["my-app.foo-ns"]
             :ns-blacklist  [] #_["taoensso.*"]

             :middleware [] ;; (fns [data]) -> ?data, applied left->right

             :timestamp-opts timbre/default-timestamp-opts ;; {:pattern _ :locale _ :timezone _}

             :output-fn timbre/default-output-fn ;; (fn [data]) -> string

             :appenders {:println (timbre/println-appender {:stream :auto})
                         ;; :spit (spit-appender {:fname "./timbre-spit.log"})
                         }}})
