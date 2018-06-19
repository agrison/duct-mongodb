(ns duct.database.mongodb.monger
  (:require [monger.core :as mg]
            [integrant.core :as ig])
  (:import (com.mongodb MongoOptions ServerAddress)))

(derive :duct.database.mongodb/monger :duct.database/mongodb)
(derive :duct.database/mongodb :duct/database)

(defrecord Boundary [conn db])

(defn- connect-with-options
  "Connect to MongoDB with specific options."
  [options host port]
  (let [^MongoOptions opts (mg/mongo-options options)
        ^ServerAddress sa (mg/server-address host port)]
    (mg/connect sa opts)))

(defn- make-connection
  "Make a MongoDB connection using either a :uri,
   or :host and :port with optional :options.

   If :uri is given then it will connect to MongoDB
   using monger.core/connect-via-uri

   Otherwise it will connect to MongoDB using
   monger.core/connect"
  [{:keys [uri options host port]}]
  (if-not (nil? uri)
    (mg/connect-via-uri uri)
    {:conn (if-not (nil? options)
             (connect-with-options options host port)
             (mg/connect {:host host :port port}))
     :db   nil}))

; Init the connection to MongoDB and return a Boundary whose keys are:
;
; :conn  which is the connection instance
; :db    the selected database if connected via :uri or
;        if :db-name was provided."
(defmethod ig/init-key :duct.database.mongodb/monger [_ conn-opts]
  (let [{:keys [conn db]} (make-connection conn-opts)
        db-name (:db-name conn-opts)
        database (or db (if-not (nil? db-name) (mg/get-db conn db-name)))]
    (->Boundary conn database)))

; Disconnects from MongoDB
(defmethod ig/halt-key! :duct.database.mongodb/monger [_ instance]
  (mg/disconnect (:conn instance)))