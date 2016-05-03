;;;;;;;;;;;;;;;;;;;;;;
;;;  Dependencies  ;;;
;;;;;;;;;;;;;;;;;;;;;;

(def common-deps '[[degree9/boot-semver "1.2.4" :scope "test"]
                   [adzerk/env          "0.3.0" :scope "test"]                  ])

(def backend-dev-deps '[])

(def backend-deps '[[org.clojure/clojure "1.8.0"]
                    [org.clojure/tools.namespace "0.2.10"]
                    [org.clojure/tools.reader "0.10.0"]
                    [org.clojure/tools.cli "0.3.3"]
                    [prismatic/schema "1.0.5"]
                    [com.taoensso/timbre "4.3.1"]
                    [mount "0.1.10"]
                    [robert/hooke "1.3.0"]
                    [cprop "0.1.7"]])
<% if any frontend %>

(def frontend-dev-deps '[[adzerk/boot-cljs "1.7.228-1" :scope "test"]
                         [adzerk/boot-cljs-repl "0.3.0" :scope "test"]
                         [adzerk/boot-reload "0.4.4" :scope "test"]
                         [deraen/boot-less "0.5.0" :scope "test"]
                         [mathias/boot-sassc "0.1.5" :scope "test"]
                         [pandeiro/boot-http "0.7.3" :scope "test"]
                         [crisptrutski/boot-cljs-test "0.2.2-SNAPSHOT" :scope "test"]
                         [adzerk/boot-cljs-repl "0.3.0" :scope "test"]
                         [com.cemerick/piggieback "0.2.1" :scope "test"]
                         [weasel "0.7.0" :scope "test"]
                         [org.clojure/tools.nrepl "0.2.12" :scope "test"]])

;; All the deps are "test" because they are only need for compiling to
;; JavaScript, not "real" project dependencies.
(def frontend-deps '[[org.clojure/clojure "1.8.0" :scope "test"]
                     [org.clojure/clojurescript "1.8.51" :scope "test"]
                     [org.clojure/core.async "0.2.374" :scope "test"]
                     [reagent "0.5.1" :exclusions [org.clojure/tools.reader] :scope "test"]
                     [reagent-forms "0.5.23" :scope "test"]
                     [reagent-utils "0.1.8" :scope "test"]
                     [hiccup "1.0.5" :scope "test"]
                     [secretary "1.2.3" :scope "test"]
                     [venantius/accountant "0.1.7" :scope "test"]
                     [com.cognitect/transit-cljs "0.8.237" :scope "test"]
                     [adzerk/cljs-console "0.1.1" :scope "test"]])
<% endif %>
(set-env! :source-paths #{"dev"}
          :dependencies common-deps)

(require 'boot
         '[clojure.pprint :refer [pprint]]
         '[clojure.string :as string]
         '[boot.pod :as pod]
         '[boot.util :as util]
         '[boot-semver.core :refer [get-version]]
         '[adzerk.env :refer [env]])

(def +version+ (get-version))

(task-options! pom {:project '<<name>>
                    :version +version+
                    :url "http://example.com/FIXME"
                    :description "FIXME: write description"
                    :license {}})

;;;;;;;;;;;;;;;;;;;;;;;
;;;  Env Variables  ;;;
;;;;;;;;;;;;;;;;;;;;;;;

(env/def
  BOOT_BUILD_FLAVOR nil)

;;;;;;;;;;;;;;;;;;;;;;;
;;  BACKEND OPTIONS  ;;
;;;;;;;;;;;;;;;;;;;;;;;

(def backend-options
  {:repl {:init-ns 'dev
          :port 5600}
   :jar {:main '<<name>>.main
         :file "<<name>>-standalone.jar"}
   :aot {:all true}})

(defmethod boot/options [:backend :dev]
  [selection]
  (merge backend-options
         {:props {"CLJS_LOG_LEVEL" "DEBUG"}
          :env {:dependencies (vec (concat backend-deps @@(resolve 'boot.repl/*default-dependencies*)))
                :middleware @@(resolve 'boot.repl/*default-middleware*)
                :source-paths #{"src/backend" "env/dev/src"}
                :resource-paths #{"env/dev/resources"}}}))

(defmethod boot/options [:backend :prod]
  [selection]
  (merge backend-options
         {:props {"CLJS_LOG_LEVEL" "INFO"}
          :env {:dependencies backend-deps
                :source-paths #{"src/backend" "env/prod/src"}
                :resource-paths #{"env/prod/resources"}}}))
<% if any frontend %>

;;;;;;;;;;;;;;;;;;;;;;;;
;;  FRONTEND OPTIONS  ;;
;;;;;;;;;;;;;;;;;;;;;;;;

(def foreign-libs
  "Specify ClojureScript foreign libs as a vector of {:file ... :provides []} maps.
  See https://github.com/clojure/clojurescript/wiki/Compiler-Options#foreign-libs"
  [])

(def prod-compiler-options
  {:closure-defines {"goog.DEBUG" false}
   :optimize-constants true
   :static-fns true
   :elide-asserts true
   :pretty-print false
   :source-map-timestamp true
   :parallel-build true
   :foreign-libs foreign-libs})

(def dev-compiler-options
  (merge prod-compiler-options
         {:pretty-print true
          :elide-asserts false
          :closure-defines {"goog.DEBUG" true}}))

(def frontend-options
  {:env {:source-paths #{"sass" "src/frontend"}
         :asset-paths #{"assets"}
         :dependencies (vec (concat frontend-dev-deps frontend-deps))}
   :reload {:on-jsload '<<name>>.app/init}
   :cljs-repl {:nrepl-opts {:port 5710}}
   :sass {:sass-file "app.scss"
          :output-dir "."
          :line-numbers true
          :source-maps true}
   :test-cljs {:optimizations :simple
               :suite-ns '<<name>>.suite}})

(defmethod boot/options [:frontend :dev]
  [selection]
  (-> frontend-options
      (update-in [:env :source-paths] conj "env/dev/src")
      (assoc :cljs {:source-map true
                    :optimizations :simple
                    :compiler-options dev-compiler-options})
      (assoc-in [:test-cljs :cljs-opts] dev-compiler-options)))

(defmethod boot/options [:frontend :prod]
  [selection]
  (-> frontend-options
      (update-in [:env :source-paths] conj "env/prod/src")
      (assoc :cljs {:source-map true
                    :optimizations :advanced
                    :compiler-options prod-compiler-options})
      (assoc-in [:test-cljs :cljs-opts] prod-compiler-options)))

(defmethod boot/options [:frontend :test]
  [selection]
  (-> frontend-options
      (assoc :cljs {:source-map true
                    :optimizations :simple
                    :compiler-options dev-compiler-options})
      (update-in [:env :source-paths] conj "test/frontend")))

<% endif %>
;;;;;;;;;;;;;;;;;;
;;  MAIN TASKS  ;;
;;;;;;;;;;;;;;;;;;

(deftask build
  "Build the final artifact.

   In order to allow task chaining (\"boot build deploy\" at the cmd line for
   instance), building all flavors at the same time is not supported at the
   moment. This means that the build task requires a --flavor and if missing it
   will read it from BOOT_BUILD_FLAVOR.

   Optionally you can specify a build type (dev or prod are supported out of
   the box). If no type is passed in, prod will be build.

   The option --o|--out-folder will keep the main.out folder in the fileset,
   which is otherwise removed."
  [f flavor VAL kw   "The flavor."
   t type   VAL kw   "The build type, either prod or dev"
   o out-folder bool "Include main.out folder."]
  (let [type (or type :prod)
        options (boot/options [flavor type])]
    (util/info "Will build the backend %s profile...\n" type)
    (case (or flavor (get (env/env) "BOOT_BUILD_FLAVOR"))
      <% if any backend %>:backend (boot/build-backend options)<% endif %><% if any frontend %>
      :frontend (boot/build-frontend options out-folder)<% endif %>
      (throw (ex-info "Cannot build without a flavor. Either specify it with -f/--flavor or set BOOT_BUILD_FLAVOR" {:flavor flavor :type type})))))

(declare dev-backend dev-frontend)

(deftask dev
  "Start the development interactive environment."
  [f flavor VAL kw "The flavor."]
  (boot.util/info "Starting interactive dev...\n")
  (case flavor
    <% if any backend %>:backend (dev-backend)<% endif %>
    <% if any frontend %>:frontend (dev-frontend)<% endif %><% if all backend frontend %>
    (comp (dev-backend)
          (dev-frontend))))
    <% endif %><% if any backend %>(dev-backend)))<% endif %>

;;;;;;;;;;;;;;;;;;;
;;  BUILD TASKS  ;;
;;;;;;;;;;;;;;;;;;;

;; see dev/boot.clj for customizations

;;;;;;;;;;;;;;;;;
;;  DEV TASKS  ;;
;;;;;;;;;;;;;;;;;

(deftask dev-backend
  "Start the development interactive environment.

   Repl in a pod, inspired by https://github.com/juxt/edge"
  []
  (let [options (boot/options [:backend :dev])
        env (:env options)
        pod-env (assoc env :directories (boot/env->directories env))
        pod (future (pod/make-pod pod-env))
        {:keys [port init-ns]} (:repl options)]

    (with-pass-thru _
      (util/info "Launching backend nRepl...\n")
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
        (require 'reloaded.repl)
        (reloaded.repl/go)))))

(deftask dev-frontend
  "Start the development interactive environment."
  []
  (let [options (boot/options [:frontend :dev])]
    (boot/apply-options! options)
    (require 'adzerk.boot-cljs
             'adzerk.boot-cljs-repl
             'adzerk.boot-reload
             'mathias.boot-sassc
             'pandeiro.boot-http)
    (let [reload (resolve 'adzerk.boot-reload/reload)
          cljs-repl (resolve 'adzerk.boot-cljs-repl/cljs-repl)
          cljs (resolve 'adzerk.boot-cljs/cljs)
          cljs-build-deps (resolve 'adzerk.boot-cljs/deps)
          serve (resolve 'pandeiro.boot-http/serve)
          sass (resolve 'mathias.boot-sassc/sass)]
      (comp (serve :dir "target")
            (watch)
            (apply sass (flatten (seq (:sass options))))
            (apply reload (flatten (seq (:reload options))))
            (apply cljs-repl (flatten (seq (:cljs-repl options))))
            (apply cljs (flatten (seq (:cljs options))))
            (if (> @boot.util/*verbosity* 1)
              (show :fileset true)
              identity)
            (target :dir #{"target"})))))

;;;;;;;;;;;;;;;;;;;;;
;;  TEST (please)  ;;
;;;;;;;;;;;;;;;;;;;;;

(ns-unmap 'boot.user 'test)

(deftask test-frontend
  "Run tests once.

   If no type is passed in, it tests against the production build. It
   optionally accepts (a set of) regular expressions that are used for testing
   only some namespaces."
  [t type       VAL        kw       "The build type, either prod or dev"
   n namespace  NAMESPACE  #{regex} "Namespace regex to test against"]
  (let [options (-> (boot/options [:frontend :test])
                    (boot/test-cljs-opts namespace true))]
    (boot/apply-options! options)
    (require 'crisptrutski.boot-cljs-test)
    (let [test-cljs (resolve 'crisptrutski.boot-cljs-test/test-cljs)]
      (comp
       (with-pass-thru _
         (util/info "Testing the frontend, prepend with watch for auto testing...\n")
         (util/dbug "Testing options:\n%s\n" (with-out-str (pprint options))))
       (apply test-cljs (flatten (seq (:test-cljs options))))))))
