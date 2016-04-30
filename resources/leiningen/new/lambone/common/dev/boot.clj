(ns boot
  (:require [boot.util :as util]
            [boot.core :as boot]))

(boot/deftask version-file
  "A task that includes the version.properties file in the fileset."
  []
  (boot/with-pre-wrap [fileset]
    (util/info "Add version.properties...\n")
    (-> fileset
        (boot/add-resource (java.io.File. ".") :include #{#"^version\.properties$"})
        boot/commit!)))

(defn set-system-properties!
  "Set a system property for each entry in the map m."
  [m]
  (doseq [kv m]
    (System/setProperty (key kv) (val kv))))

(defmulti options
  "Return the correct option map for the build, dispatching on identity"
  identity)

(defn apply-options!
  "Calls boot.core/set-env! (so don't call it twice) with the content of
  the :env key and System/setProperty for all the key/value pairs in
  the :props map."
  [options]
  (let [env (:env options)
        props (:props options)]
    (apply boot/set-env! (reduce #(into %2 %1) [] env))
    (assert (or (nil? props) (map? props)) "Option :props should be a map.")
    (set-system-properties! props)))

(defn env->directories
  "Calculate the content of :directories (a set of string) given the
  canonical (boot.core/get-env) map"
  [env]
  (reduce #(into %1 (get env %2))
          #{}
          [:source-paths :resource-paths :asset-paths]))

(defn test-cljs-opts
  [options namespaces exit?]
  (cond-> options
    namespaces (-> (update-in [:test-cljs :suite-ns] (fn [_] nil))
                   (assoc-in [:test-cljs :namespaces] namespaces))
    exit? (assoc-in [:test-cljs :exit?] exit?)))
