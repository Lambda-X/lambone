(ns leiningen.new.lambone
  {:from 'luminus-framework/luminus-template}
  (:require [clojure.set :as set]
            [clojure.string :as s]
            [clojure.pprint :refer [pprint]]
            [clojure.java.io :as io]
            [leiningen.new.templates :refer [renderer name-to-path sanitize sanitize-ns]]
            [leiningen.core.main :as main :refer [leiningen-version]]
            [leiningen.new.common :as common]
            [leiningen.new.frontend :as frontend]
            [leiningen.new.backend :as backend]))

(defn render-project
  "Create a new Lambone project, after this point the features are
  represented as keys."
  [options features]
  (let [options (assoc options :features features)
        [assets options] (-> [[] options]
                             (backend/features)
                             (frontend/features))]
    (main/info "Generating with options\n" (s/trim-newline (with-out-str (pprint options))))
    (common/render-assets (renderer "lambone" common/render-template)
                          assets
                          options)))

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

(def default-features #{"+backend"})

(def lein-min-version "2.5.2")

(defn lambone
  "Create a lambone project"
  [name & feature-params]
  (let [supported-features #{"+frontend"}
        user-features (set feature-params)
        options {:name name
                 :sanitized (name-to-path name)
                 :project-ns (sanitize-ns name)}
        unsupported (-> user-features
                        (clojure.set/difference supported-features)
                        (not-empty))]
    (main/info "Generating fresh 'lein new' lambone project.")

    (cond
      (version-before? lein-min-version)
      (main/info "Leiningen version" lein-min-version "or higher is required, found " (main/leiningen-version)
                 "\nplease run: 'lein upgrade' to upgrade Leiningen")

      (re-matches #"\A\+.+" name)
      (main/info "Project name is missing.\nTry: lein new lambone PROJECT_NAME"
                 name (clojure.string/join " " (:features options)))

      unsupported
      (main/info "Unrecognized options:" (format-features unsupported)
                 "\nSupported options are:" (format-features supported-features))

      :else (do (when (.exists (io/file name))
                  (main/warn "Overriding the directory named" name))
                (render-project options (set/union default-features user-features))))))
