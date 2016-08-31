(ns <<project-ns>>.utils
  (:require [clojure.string :as str]
            [clojure.pprint :as pprint]
            [clojure.tools.logging :as log]))

(defmacro pp-str!
  "This is not a pretty printer per se, but wraps it in order to return
  a string formatted nicely"
  [what]
  `(clojure.string/join (drop-last (with-out-str (clojure.pprint/pprint ~what)))))

(defmacro safely
  "Evaluates expr within a try/catch and returns the result.

  If present, in case of errors it executes (ex-handler ex), a function
  that accepts the exception, returning its value.
  If the ex-handler is not present it just returns the exception.

  The exception is always printed with TRACE log level."
  [expr & [ex-handler]]
  (let [ex-sym (gensym)]
    `(try
       ~expr
       (catch Throwable ~ex-sym
         (clojure.tools.logging/trace ~ex-sym "safely wrapped")
         ~(if ex-handler
            (list ex-handler ex-sym)
            ex-sym)))))

