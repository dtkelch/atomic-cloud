(ns atomic-cloud
  (:require [datomic.client.api :as d]))

(def cfg {:server-type :ion
          :region "us-east-1"
          :system "atomic-cloud"
          :endpoint "http://entry.atomic-cloud.us-east-1.datomic.net:8182/"
          :proxy-port 8182})

(def client (d/client cfg))