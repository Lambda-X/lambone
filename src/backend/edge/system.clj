(ns edge.system
  "The system, aka the core of your app"
  (:require [com.stuartsierra.component :as component]
            [cprop.core :refer [load-config]]
            [clojure.java.io :as io]
            [clojure.string :as str]
            [edge.env :as env])
  (:import java.util.Properties))

(defn version!
  "Return the current version from the version.properties file."
  []
  (let [props (Properties.)]
    (try
      ;; side effect
      (some->> "version.properties"
               io/resource
               io/reader
               (.load props))
      (catch java.io.FileNotFoundException ex
        (println "Handled exception -" (.getMessage ex))))
    (.getProperty props "VERSION")))

(defn make-config
  "Creates a default configuration map"
  []
  (-> (load-config :merge [env/defaults
                           {:version (if-let [version (version!)] version "0.0.0")}])))

(defn new-system-map [config]
  (component/system-map
   ;; put here your components
   ))

(defn new-dependency-map []
  {})

(defn new-production-system
  "Create the production system"
  []
  (-> (new-system-map)
      (component/system-using (new-dependency-map))))
