(ns leiningen.new.lambone
  (:require [clojure.java.io :as io]
            [leiningen.new.templates :refer [renderer name-to-path sanitize sanitize-ns]]
            [leiningen.core.main :as main :refer [leiningen-version]]
            [leiningen.new.common :as common]))

(def core-assets
  [[".gitignore" "common/gitignore"]
   ["build.boot" "common/build.boot"]
   ["boot.properties" "common/boot.properties"]
   ["version.properties" "common/version.properties"]
   ["README.md" "common/README.md"]
   ["LICENSE" "common/LICENSE"]

   ["env/prod/resources/config.edn" "common/env/prod/resources/config.edn"]
   ["env/dev/resources/config.edn" "common/env/dev/resources/config.edn"]
   ["env/test/resources/config.edn" "common/env/test/resources/config.edn"]

   ;; config namespaces
   ["env/dev/src/{{sanitized}}/env.cljc" "common/env/dev/src/env.cljc"]
   ["env/prod/src/{{sanitized}}/env.cljc" "common/env/prod/src/env.cljc"]

   ;; core namespaces
   ["dev/dev.clj" "common/dev/dev.clj"]
   ["dev/user.clj" "common/dev/user.clj"]
   ["dev/boot.clj" "common/dev/boot.clj"]
   ["src/frontend/{{sanitized}}/app.cljs" "common/src/frontend/app.cljs"]
   ["src/backend/{{sanitized}}/main.clj" "common/src/backend/main.clj"]
   ["src/backend/{{sanitized}}/system.clj" "common/src/backend/system.clj"]

   ;; html/css/sass stuff
   ["assets/greg.svg" "common/assets/greg.svg"]
   ["assets/index.html" "common/assets/index.html"]
   ["sass/variables.scss" "common/sass/variables.scss"]
   ["sass/app.scss" "common/sass/app.scss"]
   "assets/css"
   "assets/img"

   ;; tests
   ["test/frontend/{{sanitized}}/suite.cljs" "common/test/frontend/suite.cljs"]
   ["test/frontend/{{sanitized}}/app_test.cljs" "common/test/frontend/app_test.cljs"]
   ["test/backend/{{sanitized}}/test/system.clj" "common/test/backend/test/system.clj"]
   ["test/backend/{{sanitized}}/example_test.clj" "common/test/backend/example_test.clj"]])

(defn format-features [features]
  (apply str (interpose ", " features)))

(defn parse-version [v]
  (map #(Integer/parseInt %)
       (clojure.string/split v #"\.")))

(defn version-before? [v]
  (let [[x1 y1 z1] (parse-version (leiningen-version))
        [x2 y2 z2] (parse-version v)]
    (or
     (< x1 x2)
     (and (= x1 x2) (< y1 y2))
     (and (= x1 x2) (= y1 y2) (< z1 z2)))))

;; (def render (renderer "lambone"))
(def lein-min-version "2.5.2")

(defn lambone
  "Create a lambone project"
  [name & feature-params]
  (let [options {:name name
                 :sanitized (name-to-path name)
                 :project-ns (sanitize-ns name)}]
    (main/info "Generating fresh 'lein new' lambone project.")

    (cond
      (version-before? lein-min-version)
      (main/info "Leiningen version" lein-min-version "or higher is required, found " (main/leiningen-version)
                 "\nplease run: 'lein upgrade' to upgrade Leiningen")

      (re-matches #"\A\+.+" name)
      (main/info "Project name is missing.\nTry: lein new lambone PROJECT_NAME"
                 name (clojure.string/join " " (:features options)))

      feature-params
      (main/info "Unrecognized options:" (format-features feature-params)
                 "\nThis template does not accept options at the moment.")

      (.exists (io/file name))
      (main/info "Could not create project because a directory named" name "already exists!")

      :else
      (common/render-assets (renderer "lambone" common/render-template)
                            core-assets
                            options))))
