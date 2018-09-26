(ns atomic-cloud
  (:require [datomic.client.api :as d]))

(def cfg {:server-type :ion
          :region "us-east-2"
          :system "day-of-datomic"
          :endpoint "http://entry.day-of-datomic.us-east-2.datomic.net:8182/"
          :proxy-port 8182})

(def client (d/client cfg))

(d/create-database client {:db-name "movies"})

(def conn (d/connect client {:db-name "movies"}))


(def movie-schema [{:db/ident :movie/title
                    :db/valueType :db.type/string
                    :db/cardinality :db.cardinality/one
                    :db/doc "The title of the movie"}

                   {:db/ident :movie/genre
                    :db/valueType :db.type/string
                    :db/cardinality :db.cardinality/one
                    :db/doc "The genre of the movie"}

                   {:db/ident :movie/release-year
                    :db/valueType :db.type/long
                    :db/cardinality :db.cardinality/one
                    :db/doc "The year the movie was released in theaters"}])


(d/transact conn {:tx-data movie-schema})

(def first-movies [{:movie/title "The Goonies"
                    :movie/genre "action/adventure"
                    :movie/release-year 1985}
                   {:movie/title "Commando"
                    :movie/genre "action/adventure"
                    :movie/release-year 1985}
                   {:movie/title "Repo Man"
                    :movie/genre "punk dystopia"
                    :movie/release-year 1984}])

(d/transact conn {:tx-data first-movies})


(def all-titles-q '[:find ?movie-title
                    :where [_ :movie/title ?movie-title]])
(def db (d/db conn))

(d/q all-titles-q db)


;; (d/delete-database client {:db-name "movies"})

;; datoms are granular, atomic facts. immutable. 5-tuple: entity / attribute / value / transaction / op
