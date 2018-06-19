# Duct database.mongodb.monger

[![Build Status](https://travis-ci.org/agrison/duct-database.mongodb.monger.svg?branch=master)](https://travis-ci.org/travis-ci.org/agrison/duct-database.mongodb.monger)

[Integrant][] methods for connecting to a [MongoDB][] database via
[Monger][].

[integrant]: https://github.com/weavejester/integrant
[mongodb]: https://www.mongodb.com
[monger]: http://clojuremongodb.info

## Installation

To install, add the following to your project `:dependencies`

    [duct/database.mongodb.monger "0.1.0"]

## Usage

This library provides two things: 
* a `Boundary` record that holds
both the Monger connection (`:conn`) and the selected database (`:db`).
* a multimethod for `:duct.database.mongodb/monger` 
that initiates the connection based
on those options into the
`Boundary`.


When you write functions against the MongoDB database, consider using a
protocol and extending the `Boundary` record. This will allow you to
easily mock or stub out the database using a tool like [Shrubbery][].

[shrubbery]: https://github.com/bguthrie/shrubbery


## Connection settings

### URI

```edn
{:duct.database.mongodb/monger 
  {:uri "mongodb://127.0.0.1:27017/hello?username=foo&password=bar"}
```

### Host & port

```edn
{:duct.database.mongodb/monger 
  {:host "127.0.0.1", :port 27017, :db-name "hello"}}
```

### Host & port with extended options

```edn
{:duct.database.mongodb/monger 
  {:host "127.0.0.1", :port 27017, :db-name "hello"
   :options {:socket-timeout 1234
             :threads-allowed-to-block-for-connection-multiplier 300}}}
```

See [MongoOptions](http://api.mongodb.com/java/current/com/mongodb/MongoOptions.html) 
for more information.

## Example

Consider a MongoDB database where a `users` collection exist, 
containing documents having at least a `username` field.

The database connection can be extracted from this module
`Boundary` by using the `:db` key.

If you need access to the whole connection you can do so using
the `:conn` key.

```clojure
(ns my-project.boundary.user-db
  (:require [duct.database.mongodb.monger]
            [monger.collection :as mc]))
            
(defprotocol UserDatabase
  (get-user [db username]))
  
(extend-protocol UserDatabase
  duct.database.mongodb.monger.Boundary
  (get-user [{:keys [db]} username]
    (mc/find db "users" {:username username})))
```

For more information using Monger, you can start with their
[getting started](http://clojuremongodb.info/articles/getting_started.html)
webpage.

## Building & testing this library

If you already have a mongodb running on your machine just type
`lein test`

If you don't have mongodb installed on your machine you can type
`lein embongo test` which will download an embedded mongodb and run
it during the test phase.

## License

Copyright © 2018 Alexandre Grison

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
