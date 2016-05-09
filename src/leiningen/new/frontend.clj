(ns leiningen.new.frontend
  (:require [clojure.set :as set]
            [leiningen.new.common :as common]))

(def my-assets
  [["src/frontend/{{sanitized}}/app.cljs" "common/src/frontend/app.cljs"]

   ;; html/css/sass stuff
   ["assets/greg.svg" "common/assets/greg.svg"]
   ["assets/index.html" "common/assets/index.html"]
   ["src/frontend/css/variables.scss" "common/src/frontend/css/variables.scss"]
   ["src/frontend/css/app.scss" "common/src/frontend/css/app.scss"]
   "assets/css"
   "assets/img"

   ;; tests
   ["test/frontend/{{sanitized}}/suite.cljs" "common/test/frontend/suite.cljs"]
   ["test/frontend/{{sanitized}}/app_test.cljs" "common/test/frontend/app_test.cljs"]])

(defn features
  "Return a vector [assets options] with added stuff if necessary."
  [[assets options]]
  (if-let [features (seq (set/intersection #{"+frontend"} (:features options)))]
    [(into assets my-assets) (assoc options :frontend {:features #{:cljs-console :sass}})]
    [assets options]))
