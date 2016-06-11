(ns boot
  (:require [clojure.string :as string]
            [clojure.pprint :refer [pprint]]
            [boot.core :refer :all]
            [boot.util :as util]
            [boot.pod :as pod]
            [boot.task.built-in :as built-in]))

(defmulti options
  "Multi-method that return the correct option map for the build,
  dispatching on identity.

  The only rule that the option map needs to abide by is that each key
  must match the boot task symbol name you want to configure.

  Thence a map like

  {:repl {:init-ns 'dev
        :port 5055}
   :jar {:main '<<name>>.core
         :file \"<<name>>-standalone.jar\"}
   :aot {:all true}}

  will setup the `repl`, `jar` and `aot` boot built-in tasks
  respectively."
  identity)

(defn hotload!
  "Load a dependency on the classpath.

  It expects a (quoted) dependency vector (e.g. '[cheshire
  \"5.6.1\"]). It then resolves the corresponding jar, downloading it in
  case, and adds it to the classpath, ready to use."
  [& coords]
  (if pod/this-pod
    (let [dep-maps (pod/resolve-dependency-jars {:dependencies (vec coords)}
                                                :ignore-clj? true)]
      (run! pod/add-classpath dep-maps))
    (util/warn "Cannot hotload, not in a pod.")))

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

(def cljs-repl-deps
  '[[adzerk/boot-cljs-repl "0.3.0" :scope "test"]
    [com.cemerick/piggieback "0.2.1" :scope "test"]
    [weasel "0.7.0" :scope "test"]
    [org.clojure/tools.nrepl "0.2.12" :scope "test"]])

(def dirac-repl-deps
  '[[binaryage/devtools "0.6.1" :scope "test"]
    [binaryage/dirac "0.6.0" :scope "test"]
    [jupl/boot-cljs-devtools "0.1.1-SNAPSHOT" :scope "test"]])

(defn add-repl-deps
  [options dirac]
  (if-not dirac
    (update-in options [:env :dependencies] into cljs-repl-deps)
    (update-in options [:env :dependencies] into dirac-repl-deps)))

(defn deps
  "Return a task that shows the dependency tree from the input option
  map."
  ([options]
   (deps options false))
  ([options dirac]
   (comp (with-pass-thru _
           (let [options (add-repl-deps options dirac)]
             (util/dbug "[deps] options:\n%s\n" (with-out-str (pprint options)))
             (boot/apply-options! options)))
         (built-in/show :deps true))))

(defn env->directories
  "Calculate the content of :directories (a set of string) given the
  canonical (boot.core/get-env) map."
  [env]
  (reduce #(into %1 (get env %2))
          #{}
          [:source-paths :resource-paths :asset-paths]))

;; From danielsz/system
;; https://github.com/danielsz/system/blob/master/src/system/boot.clj
(deftask run
  "Run the -main function in some namespace with arguments

  If no -t|--type is specified the task will assume :prod."
  [m main-ns   NAMESPACE sym   "The namespace containing a -main function to invoke."
   a arguments EXPR      [str] "An optional argument sequence to apply to the -main function."
   t type      VAL       kw    "The build type, either prod or dev"]
  (let [options (options [:backend (or type :prod)])]
    (util/dbug "[run] options:\n%s\n" (with-out-str (pprint options)))
    (apply-options! options)
    (with-pass-thru _
      (require main-ns :reload)
      (if-let [f (ns-resolve main-ns '-main)]
        (apply f arguments)
        (throw (ex-info "No -main method found" {:main-ns main-ns}))))))
<% if any backend %>
(defn build-backend
  "Return a boot task for building the backend.

   The artifact is the result of (comp (aot) (uber) (jar)) but no target is
   appended."
  [options]
  (apply-options! options)
  (let [jar-name (get-in options [:jar :file])]
    (comp (with-pass-thru _
            (util/dbug "[build-backend] options:\n%s\n" (with-out-str (pprint options))))
          (version-file)
          (apply built-in/aot (mapcat identity (:aot options)))
          (apply built-in/uber (mapcat identity (:uber options)))
          (apply built-in/jar (mapcat identity (:jar options)))
          (built-in/sift :include #{(re-pattern jar-name)}))))

(defn dev-backend-pod-env
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
  (let [pod-env (dev-backend-pod-env (:env options))
        pod (future (pod/make-pod pod-env))
        {:keys [port init-ns]} (:repl options)]
    (comp
     (with-pass-thru _
       (util/dbug "[dev-backend] options:\n%s\n" (with-out-str (pprint options)))
       (util/dbug "[dev-backend] pod env:\n%s\n" (with-out-str (pprint pod-env))))
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
          (util/dbug "[test-backend] options:\n%s\n" (with-out-str (pprint options)))
          (apply-options! options)
          (require 'adzerk.boot-test))
        (with-pre-wrap fs
          (let [test (resolve 'adzerk.boot-test/test)
                middleware (apply test (mapcat identity (:test options)))]
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
            (util/dbug "[build-frontend] options:\n%s\n" (with-out-str (pprint options))))
          (version-file)
          (apply sass (mapcat identity (:sass options)))
          (apply cljs (mapcat identity (:cljs options)))
          (if-not out-folder?
            (built-in/sift :include #{#"main.out"} :invert true)
            identity))))

(defn dev-frontend
  "Start the development interactive environment."
  ([options]
   (dev-frontend options false))
  ([options dirac]
   (let [options (add-repl-deps options dirac)]
     (util/dbug "[dev-frontend] options:\n%s\n" (with-out-str (pprint options)))
     (apply-options! options)
     (require 'adzerk.boot-cljs
              (if-not dirac
                'adzerk.boot-cljs-repl
                'jupl.boot-cljs-devtools)
              'deraen.boot-sass
              'adzerk.boot-reload
              'pandeiro.boot-http)
     (let [reload (resolve 'adzerk.boot-reload/reload)
           cljs (resolve 'adzerk.boot-cljs/cljs)
           cljs-build-deps (resolve 'adzerk.boot-cljs/deps)
           sass (resolve 'deraen.boot-sass/sass)
           serve (resolve 'pandeiro.boot-http/serve)
           [repl-task repl-options] (if-not dirac
                                      [(resolve 'adzerk.boot-cljs-repl/cljs-repl) (:cljs-repl options)]
                                      [(resolve 'jupl.boot-cljs-devtools/cljs-devtools) (:cljs-devtools options)])]
       (comp (apply serve (mapcat identity (:serve options)))
             (built-in/watch)
             (version-file)
             (apply sass (mapcat identity (:sass options)))
             (apply reload (mapcat identity (:reload options)))
             (apply repl-task (mapcat identity repl-options))
             (apply cljs (mapcat identity (:cljs options)))
             (if (> @boot.util/*verbosity* 1)
               (built-in/show :fileset true)
               identity))))))

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
          (util/dbug "[test-frontend] options:\n%s\n" (with-out-str (pprint options)))
          (apply-options! options)
          (require 'crisptrutski.boot-cljs-test))
        (with-pre-wrap fs
          (let [test-cljs (resolve 'crisptrutski.boot-cljs-test/test-cljs)
                middleware (apply test-cljs (mapcat identity (:test-cljs options)))]
            ((middleware identity) fs)))))<% endif %>