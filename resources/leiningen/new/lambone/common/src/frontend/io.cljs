(ns <<project-ns>>.io
  (:require [clojure.string :as string]
            [adzerk.cljs-console :as log :include-macros true])
  (:import [goog.net XhrIo]))

(defn fetch!
  "Very simple implementation of XMLHttpRequests that given a url
  calls src-cb with the fetched stuff or nil in case of error.

  See doc at https://developers.google.com/closure/library/docs/xhrio"
  [url src-cb]
  (try
    (.send XhrIo url
           (fn [e]
             (if (.isSuccess (.-target e))
               (src-cb (.. e -target getResponseText))
               (src-cb nil))))
    (catch :default e
      (src-cb nil))))

(defn print-version!
  "Print (level info) the current version of the app."
  [version-path]
  (fetch! version-path
          (fn [content]
            (let [version (second
                           (string/split
                            (->> (string/split-lines content)
                                 (remove #(= "#" (first %)))
                                 first)
                            #"=" 2))]
              (log/info "[Version] ~{version}")))))
