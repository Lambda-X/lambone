(ns leiningen.new.common
  {:from 'luminus-framework/luminus-template}
  (:require [selmer.parser :as selmer]
            [leiningen.new.templates :refer [renderer ->files]]
            [clojure.pprint :refer [code-dispatch pprint with-pprint-dispatch]]))

(def dependency-indent 17)
(def dev-dependency-indent 33)
(def plugin-indent 12)
(def root-indent 2)
(def dev-indent 18)
(def uberjar-indent 13)
(def require-indent 13)

(defn render-template [template options]
  (selmer/render
   (str "<% safe %>" template "<% endsafe %>")
   options
   {:tag-open \< :tag-close \> :filter-open \< :filter-close \>}))

(defn render-asset [render options asset]
  (if (string? asset)
    asset
    (let [[target source] asset]
      [target (render source options)])))

(defn render-assets [render assets options]
  (apply ->files options (map (partial render-asset render options) assets)))

(defn pprint-code [code]
  (-> (pprint code)
      with-out-str
      (.replaceAll "," "")))

(defn form->str [form]
  (let [text (pprint-code form)]
    (subs text 0 (count text))))

(defn indented-code [n form]
  (let [text    (form->str form)
        indents (apply str (repeat n " "))]
    (.replaceAll (str text) "\n" (str "\n" indents))))

(defn indent [n form]
  (when form
    (let [indents (apply str (repeat n " "))]
      (if (map? form)
        (indented-code n form)
        (->> form
             (map str)
             (clojure.string/join (str "\n" indents)))))))

(defn unsupported-jetty-java-version? [java-version]
  (as-> java-version %
    (clojure.string/split % #"\.")
    (take 2 %)
    (map #(Integer/parseInt %) %)
    (and (< (first %) 2)
         (< (second %) 8))))

(defn feature->keyword
  "Drops the first char (typically a +) and creates a keyword from the
  rest of feature string."
  [feature]
  (keyword (rest feature)))
