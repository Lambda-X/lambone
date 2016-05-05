(ns <<name>>.system
  "The system, aka the core of your app"
  (:require [mount.core :as mount]
            [cprop.core :as c]
            [clojure.java.io :as io]
            [clojure.string :as str]
            [<<name>>.env :as env])
  (:import java.util.Properties))

(defn version!
  "Return the current version from the version.properties file."
  []
  (let [props (Properties.)]
    (try
      ;; side effect
      (some->> (or (io/resource "version.properties")
                   (io/file "version.properties"))
               io/reader
               (.load props))
      (catch java.io.FileNotFoundException ex
        (println "Handled exception -" (.getMessage ex))))
    (.getProperty props "VERSION")))

(defn greeting
  "Return the greeting (as string)"
  [config]
  (str (:greeting config) (when (:version config) (str " - " (:version config)))))

(defn make-config
  "Creates a default configuration map"
  []
  (-> (c/load-config :merge [env/defaults
                             {:version (if-let [version (version!)] version "0.0.0")}])))

(mount/defstate
  config
  :start (make-config))
