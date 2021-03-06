(ns spanner-test.infra.repository.user
  (:import (com.google.cloud.spanner Statement
                                     Options
                                     Options$QueryOption
                                     Mutation)
           (java.util ArrayList))
  (:require [com.stuartsierra.component :as component]))

(def table-name
  "user")

(defrecord User [id name nickname])

(defn dummy-user []
  (map->User {:id (-> (rand-int Integer/MAX_VALUE) long)
              :name (str (gensym))
              :nickname (str (gensym))}))

(defn add-user
  [{:keys [spanner] :as comp} user]
  (let [mutation (-> (Mutation/newInsertBuilder table-name)
                   (.set "id")
                   (.to (:id user))
                   (.set "name")
                   (.to (:name user))
                   (.set "nickname")
                   (.to (:nickname user))
                   (.build))]
        (-> spanner
            :dbcli
            (.write [mutation]))))

(defn example
  [{:keys [spanner] :as comp}]
  (try
    (let [session (-> spanner :dbcli .singleUse)]
      (-> session
          (.executeQuery (Statement/of "SELECT COUNT(*) FROM user")
                         (into-array Options$QueryOption [])))
      (.close session))
      (catch Exception e
      (println "Error!: " e))))

(defrecord UserRepositoryComponent [spanner]
  component/Lifecycle
  (start [this]
    (println ";; Starting UserRepositoryComponent")
    this)
  (stop [this]
    (println ";; Stopping UserRepositoryComponent")
    this))

(defn user-repository-component []
  (map->UserRepositoryComponent {}))
