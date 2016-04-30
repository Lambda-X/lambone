(ns <<project-ns>>.suite
  (:require [doo.runner :as doo :refer-macros [doo-all-tests]]
            <<project-ns>>.app-test))

(enable-console-print!)

;; Or doo will exit with an error, see:
;; https://github.com/bensu/doo/issues/83#issuecomment-165498172
(set! (.-error js/console) (fn [x] (.log js/console x)))

(doo-all-tests #"^<<project-ns>>.*-test")
