(ns leiningen.new.backend
  (:require [clojure.set :as set]
            [leiningen.new.common :as common]))

(def my-assets
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
   ["src/backend/{{sanitized}}/main.clj" "common/src/backend/main.clj"]
   ["src/backend/{{sanitized}}/system.clj" "common/src/backend/system.clj"]

   ;; tests
   ["test/backend/{{sanitized}}/test/system.clj" "common/test/backend/test/system.clj"]
   ["test/backend/{{sanitized}}/example_test.clj" "common/test/backend/example_test.clj"]])

(defn features
  "Return a vector [assets options] with added stuff if necessary."
  [[assets options]]
  (if-let [features (seq (set/intersection #{"+backend"} (:features options)))]
    [(into assets my-assets) (assoc options :backend {:mount true
                                                      :timbre true})]
    [assets options]))
