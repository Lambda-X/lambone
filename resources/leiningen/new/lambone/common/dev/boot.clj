(ns boot
  (:require [clojure.string :as string]
            [clojure.pprint :refer [pprint]]
            [boot.core :refer :all]
            [boot.util :as util]
            [boot.pod :as pod]
            [boot.task.built-in :as built-in]))

(deftask version-file
  "A task that includes the version.properties file in the fileset."
  []
  (with-pre-wrap [fileset]
    (util/info "Add version.properties...\n")
    (-> fileset
        (add-resource (java.io.File. ".") :include #{#"^version\.properties$"})
        commit!)))

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
    (apply set-env! (reduce #(into %2 %1) [] env))
    (assert (or (nil? props) (map? props)) "Option :props should be a map.")
    (set-system-properties! props)))

(defn env->directories
  "Calculate the content of :directories (a set of string) given the
  canonical (boot.core/get-env) map"
  [env]
  (reduce #(into %1 (get env %2))
          #{}
          [:source-paths :resource-paths :asset-paths]))
<% if any backend %>
(defn build-backend
  "Return a boot task for building the backend.

   The artifact is the result of (comp (aot) (uber) (jar)) but no target is
   appended."
  [options]
  (apply-options! options)
  (let [jar-name (get-in options [:jar :file])]
    (comp (with-pass-thru _
            (util/dbug "Options:\n%s\n" (with-out-str (pprint options))))
          (version-file)
          (apply built-in/aot (flatten (seq (:aot options))))
          (apply built-in/uber (flatten (seq (:uber options))))
          (apply built-in/jar (flatten (seq (:jar options))))
          (built-in/sift :include #{(re-pattern jar-name)}))))

(defn make-pod-env
  [current-env]
  (assoc current-env
         :directories (boot/env->directories current-env)
         :dependencies (concat (:dependencies current-env)
                               @@(resolve 'boot.repl/*default-dependencies*))
         :middleware @@(resolve 'boot.repl/*default-middleware*)))

(defn dev-backend
  "Start the development interactive environment.

   Repl in a pod, inspired by https://github.com/juxt/edge"
  [options]
  (util/dbug "Options:\n%s\n" (with-out-str (pprint options)))
  (let [pod-env (make-pod-env (:env options))
        pod (future (pod/make-pod pod-env))
        {:keys [port init-ns]} (:repl options)]
    (comp
     (with-pass-thru _
       (util/dbug "Options:\n%s\n" (with-out-str (pprint options)))
       (util/dbug "Pod env:\n%s\n" (with-out-str (pprint pod-env))))
     (with-pass-thru _
       (pod/with-eval-in @pod
         (require '[boot.pod :as pod])
         (require '[boot.util :as util])
         (require '[boot.repl :as repl])
         (require '[clojure.tools.namespace.repl :as tnsr])

         (apply tnsr/set-refresh-dirs (-> pod/env :directories))
         (repl/launch-nrepl {:init-ns '~init-ns
                             :port '~port
                             :server true
                             :middleware (:middleware pod/env)})
         ;; Auto-start the system
         (require 'dev)
         (dev/go))))))

(defn boot-test-opts
  [options namespaces exclusions]
  (cond-> options
    namespaces (assoc-in [:test :namespaces] namespaces)
    exclusions (assoc-in [:test :exclusions] exclusions)))

(defn test-backend
  "Run tests once for the backend (uses clojure.test)."
  [options]
  (comp (with-pass-thru _
          (util/dbug "Options:\n%s\n" (with-out-str (pprint options)))
          (apply-options! options)
          (require 'adzerk.boot-test))
        (with-pre-wrap fs
          (let [test (resolve 'adzerk.boot-test/test)
                middleware (apply test (flatten (seq (:test options))))]
            ((middleware identity) fs)))))<% endif %>
<% if any frontend %>
(defn build-frontend
  "Return a boot task for building the frontend.

  The folder main.out is excluded by default unless out-folder? is
  specified."
  [options out-folder?]
  (apply-options! options)
  (require 'adzerk.boot-cljs
           'deraen.boot-sass)
  (let [cljs (resolve 'adzerk.boot-cljs/cljs)
        sass (resolve 'deraen.boot-sass/sass)]
    (comp (with-pass-thru _
            (util/dbug "Options:\n%s\n" (with-out-str (pprint options))))
          (version-file)
          (apply sass (flatten (seq (:sass options))))
          (apply cljs (flatten (seq (:cljs options))))
          (if-not out-folder?
            (built-in/sift :include #{#"main.out"} :invert true)
            identity))))

(defn dev-frontend
  "Start the development interactive environment."
  [options]
  (util/dbug "Options:\n%s\n" (with-out-str (pprint options)))
  (apply-options! options)
  (require 'adzerk.boot-cljs
           'adzerk.boot-cljs-repl
           'deraen.boot-sass
           'adzerk.boot-reload)
  (let [reload (resolve 'adzerk.boot-reload/reload)
        cljs-repl (resolve 'adzerk.boot-cljs-repl/cljs-repl)
        cljs (resolve 'adzerk.boot-cljs/cljs)
        cljs-build-deps (resolve 'adzerk.boot-cljs/deps)
        sass (resolve 'deraen.boot-sass/sass)]
    (comp (built-in/watch)
          (version-file)
          (apply sass (flatten (seq (:sass options))))
          (apply reload (flatten (seq (:reload options))))
          (apply cljs-repl (flatten (seq (:cljs-repl options))))
          (apply cljs (flatten (seq (:cljs options))))
          (if (> @boot.util/*verbosity* 1)
            (built-in/show :fileset true)
            identity))))

(defn boot-cljs-test-opts
  [options namespaces]
  (cond-> options
    namespaces (-> (update-in [:test-cljs :suite-ns] (fn [_] nil))
                   (assoc-in [:test-cljs :namespaces] namespaces))))

(defn test-frontend
  "Run tests once.

  If no type is passed in, it tests against the production build. It
  optionally accepts (a set of) symbols that are used for testing only
  some namespaces."
  [options]
  (comp (with-pass-thru _
          (util/dbug "Options:\n%s\n" (with-out-str (pprint options)))
          (apply-options! options)
          (require 'crisptrutski.boot-cljs-test))
        (with-pre-wrap fs
          (let [test-cljs (resolve 'crisptrutski.boot-cljs-test/test-cljs)
                middleware (apply test-cljs (flatten (seq (:test-cljs options))))]
            ((middleware identity) fs)))))<% endif %>