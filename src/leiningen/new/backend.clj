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

   ["env/prod/resources/log4j2.xml" "common/env/prod/resources/log4j2.xml"]
   ["env/dev/resources/log4j2.xml" "common/env/dev/resources/log4j2.xml"]
   ["env/prod/resources/config.edn" "common/env/prod/resources/config.edn"]
   ["env/dev/resources/config.edn" "common/env/dev/resources/config.edn"]
   ["env/test/resources/config.edn" "common/env/test/resources/config.edn"]

   ["dev/boot.clj" "common/dev/boot.clj"]
   ["env/dev/src/user.clj" "common/env/dev/src/user.clj"]
   ["env/dev/src/dev.clj" "common/env/dev/src/dev.clj"]
   ["env/dev/src/{{sanitized}}/env.cljc" "common/env/dev/src/env.cljc"]
   ["env/prod/src/{{sanitized}}/env.cljc" "common/env/prod/src/env.cljc"]
   ["env/test/src/{{sanitized}}/env.cljc" "common/env/test/src/env.cljc"]

   ["src/backend/{{sanitized}}/core.clj" "common/src/backend/core.clj"]
   ["src/backend/{{sanitized}}/system.clj" "common/src/backend/system.clj"]
   ["src/backend/{{sanitized}}/logging.clj" "common/src/backend/logging.clj"]

   ;; tests
   ["test/backend/{{sanitized}}/system_test.clj" "common/test/backend/system_test.clj"]])

(defn features
  "Return a vector [assets options] with added stuff if necessary."
  [[assets options]]
  (if-let [features (seq (set/intersection #{"+backend"} (:features options)))]
    [(into assets my-assets) (assoc options :backend {:features #{:mount :log4j2}})]
    [assets options]))
