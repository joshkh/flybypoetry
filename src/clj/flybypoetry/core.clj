(ns flybypoetry.core
  (:use [clj-wordnet.core])
  (:require [clojure.data.csv :as csv]
            [clojure.java.io :as io]
            [clojure.data.json :as json]
            [ring.adapter.jetty :refer [run-jetty]]
            [ring.middleware.resource :refer [wrap-resource]]
            [ring.util.http-response :refer :all]
            [environ.core :refer [env]]
            [compojure.api.sweet :refer :all]))

(def wordnet (make-dictionary "./data/dict"))

(def dog (first (wordnet "dog")))

(def madeup (first (wordnet "abcdef" :noun)))

(defn lookup-symbol [[symbol db-identifier]]
 (let [result (first (wordnet symbol))]
   (if result
     {:symbol symbol
      :pos (:pos result)
      :db-identifier db-identifier})))

(def dictionary (atom nil))


(defn rand-verb []
  (rand-nth (filter #(= :verb (:pos %)) @dictionary)))

(defn rand-adverb []
  (rand-nth (filter #(= :adverb (:pos %)) @dictionary)))

(defn rand-noun []
  (rand-nth (filter #(= :noun (:pos %)) @dictionary)))

(defn rand-adjective []
  (rand-nth (filter #(= :adjective (:pos %)) @dictionary)))

(defn rand-single-letter []
  (rand-nth (filter #(= 1 (count (:symbol %))) @dictionary)))


(defn grab-bag []
  (concat
   (take 20 (repeatedly rand-noun))
   (take 20 (repeatedly rand-verb))
   (take 10 (repeatedly rand-adjective))
   (take 10 (repeatedly rand-single-letter))))

(defn build []
  (println "Building map.")
  (with-open [in-file (io/reader "./data/symbols.csv")]
    (reset! dictionary (doall (filter identity (map lookup-symbol (csv/read-csv in-file))))))
  (println "COUNT" (count @dictionary)))


(def dict (atom nil))
(def letters (map char (range (int \a) (inc (int \z)))))

(defn build-json-db []
  (println "building json db")
  (doall (for [letter letters]
    (let [records (json/read-str (slurp (str "./data/gcide_" (str letter) "-entries.json")) :key-fn keyword)]
      (println "COUNT" (count records))
      (doall (for [[word vals] records]
        (swap! dict assoc word {:pos (distinct (map :part_of_speech (:definitions vals)))}))))))
  (println (count @dict))
  ; Compare our genes to the json db
  (println "FILTERED" (count (doall (filter (fn [[symbol id]]
                   (contains? @dict (keyword symbol))) (csv/read-csv (io/reader "./data/symbols.csv"))))))
  )



; (build-json-db)

(build)

(def app
  (api
   (context "/api" []
            :tags ["api"]

            (GET "/courses" []
                 ;  :return [Course]
                 :summary "adds two numbers together"
                 (ok (grab-bag))))))


(defn run-web-server [& [port]]
  (let [port (Integer. (or port (env :port) 5000))]
    ; (println "SEEDING")
    ; (println (seeder/seed))
    ; (info (format "Starting web server on port %d." port))
    (println "started")
    (run-jetty (-> app (wrap-resource "public")) {:port port})))

(defn run [& [port]]
  (run-web-server port))

  (defn -main [& [port]]
    ; (seed/seed)

    ; (println "testing grab bag" (grab-bag))
    (run port))
