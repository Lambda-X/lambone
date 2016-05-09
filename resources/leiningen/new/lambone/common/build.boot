;;;;;;;;;;;;;;;;;;;;;;
;;;  Dependencies  ;;;
;;;;;;;;;;;;;;;;;;;;;;

(def cmd-line-deps '[[degree9/boot-semver "1.2.4" :scope "test"]
                     [adzerk/env "0.3.0" :scope "test"]
                     [pandeiro/boot-http "0.7.3" :scope "test"]])

(def common-deps '[[org.clojure/clojure "1.8.0" :scope "test"]])

(def backend-dev-deps '[[adzerk/boot-test "1.1.1"]
                        [adzerk/env "0.3.0" :scope "test"]])

(def backend-deps (into common-deps
                        '[[org.clojure/tools.namespace "0.2.10"]
                          [org.clojure/tools.reader "0.10.0"]
                          [org.clojure/tools.cli "0.3.3"]
                          [org.clojure/tools.logging "0.3.1"]
                          [org.apache.logging.log4j/log4j-api "2.5" :scope "runtime"]
                          [org.apache.logging.log4j/log4j-core "2.5" :scope "runtime"]
                          [org.apache.logging.log4j/log4j-jcl "2.5" :scope "runtime"]
                          [org.apache.logging.log4j/log4j-jul "2.5" :scope "runtime"]
                          [org.apache.logging.log4j/log4j-1.2-api "2.5" :scope "runtime"]
                          [org.apache.logging.log4j/log4j-slf4j-impl "2.5" :scope "runtime"]
                          [mount "0.1.10"]
                          [robert/hooke "1.3.0"]
                          [cprop "0.1.7"]]))
<% if any frontend %>
(def frontend-dev-deps '[[adzerk/boot-cljs "1.7.228-1" :scope "test"]
                         [adzerk/boot-cljs-repl "0.3.0" :scope "test"]
                         [adzerk/boot-reload "0.4.4" :scope "test"]
                         [deraen/boot-sass "0.2.1" :scope "test"]
                         [crisptrutski/boot-cljs-test "0.2.2-SNAPSHOT" :scope "test"]
                         [adzerk/boot-cljs-repl "0.3.0" :scope "test"]
                         [com.cemerick/piggieback "0.2.1" :scope "test"]
                         [weasel "0.7.0" :scope "test"]
                         [org.clojure/tools.nrepl "0.2.12" :scope "test"]])

;; All the deps are "test" because they are only need for compiling to
;; JavaScript, not "real" project dependencies.
(def frontend-deps (into common-deps
                         '[[org.clojure/clojurescript "1.8.51" :scope "test"]
                           [org.clojure/core.async "0.2.374" :scope "test"]
                           [reagent "0.5.1" :exclusions [org.clojure/tools.reader] :scope "test"]
                           [reagent-forms "0.5.23" :scope "test"]
                           [reagent-utils "0.1.8" :scope "test"]
                           [hiccup "1.0.5" :scope "test"]
                           [secretary "1.2.3" :scope "test"]
                           [venantius/accountant "0.1.7" :scope "test"]
                           [com.cognitect/transit-cljs "0.8.237" :scope "test"]
                           [adzerk/cljs-console "0.1.1" :scope "test"]]))
<% endif %>
(set-env! :source-paths #{"dev"}
          :dependencies cmd-line-deps)

(require 'boot
         '[clojure.pprint :refer [pprint]]
         '[clojure.string :as string]
         '[boot.pod :as pod]
         '[boot.util :as util]
         '[boot-semver.core :refer [get-version]]
         '[pandeiro.boot-http :refer [serve]]
         '[adzerk.env :as env])

(def +version+ (get-version))

(task-options! pom {:project '<<name>>
                    :version +version+
                    :url "http://example.com/FIXME"
                    :description "FIXME: write description"
                    :license {}})

;;;;;;;;;;;;;;;;;;;;;;;
;;;   Environment   ;;;
;;;;;;;;;;;;;;;;;;;;;;;

;; Note that these are treated as system properties, see https://github.com/adzerk-oss/env/issues/2
;; and adopt the syntax for cprop: https://github.com/tolitius/cprop#system-properties-cprop-syntax

(env/def
  BOOT_BUILD_FLAVOR <% if all backend frontend%>nil<% endif%><% if any backend %>"backend"<% endif%>)

;;;;;;;;;;;;;;;;;;;;;;;
;;  BACKEND OPTIONS  ;;
;;;;;;;;;;;;;;;;;;;;;;;

(def backend-options
  {:repl {:init-ns 'dev
          :port 5055}
   :jar {:main '<<name>>.core
         :file "<<name>>-standalone.jar"}
   :aot {:all true}})

(def backend-dev-dependencies
  (vec (distinct (concat backend-dev-deps backend-deps))))

(defmethod boot/options [:backend :dev]
  [selection]
  (merge backend-options
         {:env {:dependencies backend-dev-dependencies
                :source-paths #{"src/backend" "env/dev/src"}
                :resource-paths #{"env/dev/resources"}}}))

(defmethod boot/options [:backend :prod]
  [selection]
  (merge backend-options
         {:env {:dependencies backend-deps
                :source-paths #{"src/backend" "env/prod/src"}
                :resource-paths #{"env/prod/resources"}}}))

(defmethod boot/options [:backend :test]
  [selection]
  (merge backend-options
         {:test {:namespaces #{'acu.system-test}}
          :env {:dependencies backend-dev-dependencies
                :middleware @@(resolve 'boot.repl/*default-middleware*)
                :source-paths #{"src/backend" "test/backend" "env/dev/src"}
                :resource-paths #{"env/dev/resources"}}}))
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
   :cljs-repl {:nrepl-opts {:port 5088}}
   :sass {:sass-file "app.scss"
          :output-dir "."
          :line-numbers true
          :source-maps true}
   :test-cljs {:optimizations :simple
               :suite-ns '<<name>>.suite}})

(defmethod boot/options [:frontend :dev]
  [selection]
  (-> frontend-options
      (merge {:props {"CLJS_LOG_LEVEL" "DEBUG"}
              :cljs {:source-map true
                     :optimizations :simple
                     :compiler-options dev-compiler-options}})
      (update-in [:env :source-paths] conj "env/dev/src")
      (assoc :cljs {:source-map true
                    :optimizations :simple
                    :compiler-options dev-compiler-options})
      (assoc-in [:test-cljs :cljs-opts] dev-compiler-options)))

(defmethod boot/options [:frontend :prod]
  [selection]
  (-> frontend-options
      (merge {:props {"CLJS_LOG_LEVEL" "INFO"}
              :cljs {:source-map true
                     :optimizations :advanced
                     :compiler-options prod-compiler-options}})
      (update-in [:env :source-paths] conj "env/prod/src")
      (assoc :cljs {:source-map true
                    :optimizations :advanced
                    :compiler-options prod-compiler-options})
      (assoc-in [:test-cljs :cljs-opts] prod-compiler-options)))

(defmethod boot/options [:frontend :test]
  [selection]
  (-> frontend-options
      (merge {:props {"CLJS_LOG_LEVEL" "DEBUG"}
              :cljs {:source-map true
                     :optimizations :simple
                     :compiler-options dev-compiler-options}})
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
  [f flavor VAL kw   "The flavor"
   t type   VAL kw   "The build type, either prod or dev"
   o out-folder bool "Include main.out folder."]
  (let [type (or type :prod)
        flavor (or flavor (keyword (get (env/env) "BOOT_BUILD_FLAVOR")))
        options (boot/options [flavor type])]
    (util/dbug "Options:\n%s\n" (with-out-str (pprint options)))
    (util/info "Will build the [%1s %2s] profile...\n" flavor type)
    (case flavor
      <% if any backend %>:backend (boot/build-backend options)<% endif %><% if any frontend %>
      :frontend (boot/build-frontend options out-folder)<% endif %>
      (throw (ex-info "Cannot build without a flavor. Either specify it with -f/--flavor or set BOOT_BUILD_FLAVOR" {:flavor flavor :type type})))))

(deftask dev
  "Start the development interactive environment.

  If no flavor is specified, both sessions will be started. The defaults are:
  - port 5055 for the backend
  - port 5088 for the frontend (call adzerk.boot-cljs-repl/start-repl and then
  point your browser to http://localhost:3000)."
  [f flavor VAL kw "The flavor (backend or frontend)"]
  (boot.util/info "Starting interactive dev...\n")
  (let [<% if any backend %>dev-backend #(boot/dev-backend (boot/options [:backend :dev]))<% endif %><% if any frontend %>
        dev-frontend #(boot/dev-frontend (boot/options [:frontend :dev]))<% endif %>]
    (case flavor
      <% if any backend %>:backend (comp (dev-backend) (wait))<% endif %><% if any frontend %>
      :frontend (dev-frontend)<% endif %><% if all backend frontend %>
      (comp (dev-backend)
            (dev-frontend)))))<% endif %><% if not all backend frontend %>
(comp (dev-backend)
      (wait)))))<% endif %>

(ns-unmap 'boot.user 'test)

(deftask test
  "Run tests once.

   If no flavor is specified, all the tests for all the flavors will be
   triggered.

   If no type is passed in, it tests against the production build.

   If no namespace option is specified, it tests all the namespaces in the
   classpath except the symbols in the exclusion set.

   In order to enable auto testing, prepend this task with watch, e.g. boot
   watch test."
  [f flavor     VAL        kw        "The flavor"
   n namespace  NS         #{sym}   "Override and test only this namespace"
   e exclusion  REGEX      #{sym}   "Exclude this namespace"]
  (let [<% if any backend %>test-backend #(-> (boot/options [:backend :test])
                                              (boot/boot-test-opts namespace exclusion)
                                              boot/test-backend)<% endif %><% if any frontend %>
        test-frontend #(-> (boot/options [:frontend :test])
                           (boot/boot-cljs-test-opts namespace true)
                           boot/test-frontend)<% endif %>]
    (case flavor
      <% if any backend %>:backend (test-backend)<% endif %>
      <% if any frontend %>:frontend (test-frontend)<% endif %><% if all backend frontend %>
      (comp (test-backend)
            (test-frontend)))))
<% endif %><% if not all backend frontend %>(test-backend))))<% endif %>

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;  see dev/boot.clj for task customization  ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
