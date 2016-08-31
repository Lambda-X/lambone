(ns <<project-ns>>.utils-test
  (:require [clojure.test :as t :refer [deftest is]]
            [<<project-ns>>.utils :as utils]))

(deftest safely-return
  (is (= 1 (utils/safely (/ 1 1) (fn [ex] nil))) "Safely should return the result of the function if no exception are thrown")
  (is (nil? (utils/safely (/ 1 0) (fn [ex] nil))) "Safely should return what is passed in the exception handler in case of error"))
