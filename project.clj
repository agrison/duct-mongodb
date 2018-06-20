(defproject me.grison/duct-mongodb "0.1.0"
  :description "Integrant methods for connecting to MongoDB via Monger"
  :url "https://github.com/agrison/duct-mongodb"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [integrant "0.6.3"]
                 [com.novemberain/monger "3.1.0"]]
  :plugins [[lein-embongo "0.2.3"]])
