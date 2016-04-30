(ns edge.suite
  (:require [doo.runner :as doo :refer-macros [doo-all-tests]]
            edge.main-test))

(enable-console-print!)

;; Or doo will exit with an error, see:
;; https://github.com/bensu/doo/issues/83#issuecomment-165498172
(set! (.-error js/console) (fn [x] (.log js/console x)))

(doo-all-tests #"^edge.*-test")
