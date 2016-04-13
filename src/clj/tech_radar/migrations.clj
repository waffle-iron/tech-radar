(ns tech-radar.migrations
  (:require [environ.core :refer [env]]
            [ragtime.jdbc :as jdbc]
            [ragtime.repl :as repl]))

(defn- get-config []
  {:datastore  (jdbc/sql-database {:connection-uri (env :database)})
   :migrations (jdbc/load-resources "migrations")})

;TODO support UTF-8
(defn migrate []
  (-> (get-config)
      (repl/migrate)))

(defn rollback []
  (-> (get-config)
      (repl/rollback)))