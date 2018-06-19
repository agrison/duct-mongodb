(ns duct.database.mongodb.monger-test
  (:require [clojure.test :refer :all]
            [duct.database.mongodb.monger :refer :all]
            [integrant.core :as ig])
  (:import (com.mongodb DB MongoClient)))

; by default a `test` database` exists in MongoDB.

(deftest key-derive-test
  (is (isa? :duct.database.mongodb/monger :duct.database/mongodb))
  (is (isa? :duct.database/mongodb :duct/database)))

(defn- test-connection [{:keys [conn db]}]
  (is (instance? MongoClient conn))
  (is (instance? DB db))
  (is (= "test" (.getName db))))

(defn- test-disconnection [mongo]
  (do
    (ig/halt-key! :duct.database.mongodb/monger mongo)
    ; ensure is closed
    (is (thrown? Exception (.getAddress (:conn mongo))))))

(deftest init-key-uri-test
  (let [mongo (ig/init-key :duct.database.mongodb/monger
                           {:uri "mongodb://127.0.0.1:27017/test"})]
    (test-connection mongo)
    (test-disconnection mongo)))

(deftest init-key-host-and-port-test
  (let [mongo (ig/init-key :duct.database.mongodb/monger
                           {:host "127.0.0.1" :port 27017 :db-name "test"})]
    (test-connection mongo)
    (test-disconnection mongo)))

(deftest init-key-host-and-port-options-test
  (let [mongo (ig/init-key :duct.database.mongodb/monger
                           {:host "127.0.0.1" :port 27017 :db-name "test"
                            :options {:socket-timeout 123}})]
    (test-connection mongo)
    (is (= 123 (.socketTimeout (.getMongoOptions (:conn mongo)))))
    (test-disconnection mongo)))
