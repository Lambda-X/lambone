(ns <<project-ns>>.system-test
  (:require [mount.core :as mount]
            [clojure.test :refer :all]
            [<<project-ns>>.system :refer :all]))

(defn with-parts [f]
  (mount/start #'<<project-ns>>.system/config)
  (f)
  (mount/stop))

(use-fixtures :once with-parts)

(deftest start-only-parts 
  (is (map? config) "Config should be a map")
  (is (= (:version config) (version!)) "Config version should match version.properties"))

