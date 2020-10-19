(ns duct.database.mongodb.monger
  (:require [monger.core :as mg]
            [monger.credentials :as mcred]
            [integrant.core :as ig])
  (:import (com.mongodb MongoClient
                        MongoCredential
                        MongoOptions
                        ServerAddress)))

(derive :duct.database.mongodb/monger :duct.database/mongodb)
(derive :duct.database/mongodb :duct/database)

(defrecord Boundary [conn db])

(defn- make-connection
  "Make a MongoDB connection using either a :uri,
   or :host and :port with optional credentials for
   basic authentication :username, :db-name and :password,
   and optional :options.

   If :uri is given then it will connect to MongoDB
   using monger.core/connect-via-uri

   Otherwise it will connect to MongoDB using
   monger.core/connect"
  [{:keys [uri options host port db-name username password]
    :as config}]
  (if uri
    (mg/connect-via-uri uri)
    (let [^ServerAddress sa
          (when host (mg/server-address host port))
          ^MongoOptions opts
          (mg/mongo-options (or options {}))
          ^MongoCredential creds
          (when (and username db-name)
            (mcred/create username db-name password))
          ^MongoClient conn
          (cond
            creds (mg/connect sa opts creds)
            options (mg/connect sa opts)
            host (mg/connect (select-keys config [host port]))
            :else nil)]
      {:conn conn
       :db   (when db-name (mg/get-db conn db-name))})))

; Init the connection to MongoDB and return a Boundary whose keys are:
;
; :conn  which is the connection instance
; :db    the selected database if connected via :uri or
;        if :db-name was provided."
(defmethod ig/init-key :duct.database.mongodb/monger [_ conn-opts]
  (map->Boundary (make-connection conn-opts)))

; Disconnects from MongoDB
(defmethod ig/halt-key! :duct.database.mongodb/monger [_ {:keys [conn]}]
  (when conn (mg/disconnect conn)))
