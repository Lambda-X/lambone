(set-env! :dependencies '[[adzerk/env "0.3.0" :scope "test"]])

(require '[adzerk.env :as env])

;;;;;;;;;;;;;;;;;;;;;;;
;;;   Environment   ;;;
;;;;;;;;;;;;;;;;;;;;;;;

;; Note that these are treated as system properties, see https://github.com/adzerk-oss/env/issues/2
;; and adopt the syntax for cprop: https://github.com/tolitius/cprop#system-properties-cprop-syntax

#_(env/def
    DATABASE_URL  "jdbc:postgresql://localhost:5432/<<project-ns>>"
    DATABASE_USERNAME "fill-me-up"
    DATABASE_PASSWORD "fill-me-up")
