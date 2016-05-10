(ns <<project-ns>>.app-test
  (:require [cljs.test :refer-macros [deftest is async]]
            [<<project-ns>>.app :as app]))

(deftest sanity-check
  (is (= 1 1)))

;; AR - Straight from doo's examples
(deftest async-test
  (async done
    (let [a 1]
      (js/setTimeout (fn []
                       (is (= 1 a))
                       (done))
                     100)
      (is (= 1 a)))))
