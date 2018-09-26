(ns atomic-cloud
  (:require [datomic.client.api :as d]))

(def cfg {:server-type :ion
          :region "us-east-2"
          :system "day-of-datomic"
          :endpoint "http://entry.day-of-datomic.us-east-2.datomic.net:8182/"
          :proxy-port 8182})

(def client (d/client cfg))

;; (d/create-database client {:db-name "movies"})

;; (def conn (d/connect client {:db-name "movies"}))


;; (def movie-schema [{:db/ident :movie/title
;;                     :db/valueType :db.type/string
;;                     :db/cardinality :db.cardinality/one
;;                     :db/doc "The title of the movie"}

;;                    {:db/ident :movie/genre
;;                     :db/valueType :db.type/string
;;                     :db/cardinality :db.cardinality/one
;;                     :db/doc "The genre of the movie"}

;;                    {:db/ident :movie/release-year
;;                     :db/valueType :db.type/long
;;                     :db/cardinality :db.cardinality/one
;;                     :db/doc "The year the movie was released in theaters"}])


;; (d/transact conn {:tx-data movie-schema})

;; (def first-movies [{:movie/title "The Goonies"
;;                     :movie/genre "action/adventure"
;;                     :movie/release-year 1985}
;;                    {:movie/title "Commando"
;;                     :movie/genre "action/adventure"
;;                     :movie/release-year 1985}
;;                    {:movie/title "Repo Man"
;;                     :movie/genre "punk dystopia"
;;                     :movie/release-year 1984}])

;; (d/transact conn {:tx-data first-movies})


;; (def all-titles-q '[:find ?movie-title
;;                     :where [_ :movie/title ?movie-title]])
;; (def db (d/db conn))

;; (d/q all-titles-q db)


;; (d/delete-database client {:db-name "movies"})

;; datoms are granular, atomic facts. immutable. 5-tuple: entity / attribute / value / transaction / op
;; databases are a set of datoms - it's a universal relation. all stored in the same group. not in separate tables (specific relation).


(d/create-database client {:db-name "orders"})
(def conn (d/connect client {:db-name "orders"}))

(def order-schema [{:db/ident :order/identifier
                    :db/valueType :db.type/string
                    :db/cardinality :db.cardinality/one
                    :db/unique :db.unique/identity
                    :db/doc "Order Identifier"}

                   {:db/ident :order/date
                    :db/valueType :db.type/instant
                    :db/cardinality :db.cardinality/one
                    :db/doc "Order Date"}

                   {:db/ident :order/type
                    :db/valueType :db.type/ref
                    :db/cardinality :db.cardinality/one
                    :db/doc "Order Type"}

                   {:db/ident :order/customer
                    :db/valueType :db.type/ref
                    :db/cardinality :db.cardinality/one
                    :db/doc "Customer"}
                   ])
(d/transact conn {:tx-data order-schema})

(def order-type-schema [{:db/ident :order-type/type
                                  :db/valueType :db.type/string
                                  :db/cardinality :db.cardinality/one
                                  :db/unique :db.unique/identity
                                  :db/doc "Order Type Identifier"}
                                 ])
(d/transact conn {:tx-data order-type-schema})

(def customer-schema [{:db/ident :customer/identifier
                       :db/valueType :db.type/string
                       :db/cardinality :db.cardinality/one
                       :db/unique :db.unique/identity
                       :db/doc "Customer Identifier"}

                      {:db/ident :customer/name
                       :db/valueType :db.type/string
                       :db/cardinality :db.cardinality/one
                       :db/doc "Customer Name"}
                      ])

(d/transact conn {:tx-data customer-schema})

(def customers    [{:customer/identifier "MMM"
                    :customer/name "Meijer"}
                   {:customer/identifier "CCC"
                    :customer/name "Costco"}
                   {:customer/identifier "HHH"
                    :customer/name "Home Depot"}])

(def order-types    [{:order-type/type "Customer Order"}
                     {:order-type/type "Distribution Order"}
                     {:order-type/type "Supplier Order"}])

(defn now [] (new java.util.Date))


(defn get-customer-id [identifier]
  (ffirst (d/q '[:find ?e
                 :in $ ?identifier
                 :where [?e :customer/identifier ?identifier]] db identifier))
  )

(def orders
  [
   {:order/identifier "ABC123"
    :order/date (now)
    :order/customer (get-customer-id "MMM")
    ;; :order/type :order-type/"Customer Order"
    }
   ;; {:order/identifier "DST123"
   ;;  :order/date (now)
   ;;  :order/customer {:customer/identifier "MMM"}
   ;;  :order/type { :order-type/type "Distribution Order" }
   ;;  }
   ;; {:order/identifier "DEF456"
   ;;  :order/date (now)
   ;;  :order/customer {:customer/identifier "HHH"}
   ;;  :order/type { :order-type/type "Customer Order" }
   ;;  }
   ])

(d/transact conn {:tx-data customer-schema})
(d/transact conn {:tx-data customers})
(d/transact conn {:tx-data orders})
(d/transact conn {:tx-data order-types})
(d/delete-database client {:db-name "orders"})

(def all-customers-q '[:find ?identifier
                    :where [_ :customer/identifier ?identifier]])
(def db (d/db conn))

(d/q all-customers-q db)

(d/q
 '[:find ?identifier
    :where [_ :customer/identifier ?identifier]]
 db)

(d/q
 '[:find ?identifier ?customer ?date ?type
   :where [_ :customer/identifier ?identifier]]
 db)
