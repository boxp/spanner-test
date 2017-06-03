(ns spanner-test.system
  (:require [com.stuartsierra.component :as component]
            [environ.core :refer [env]]
            [spanner-test.infra.datastore.spanner :refer [spanner-component]]
            [spanner-test.infra.repository.user :refer [user-repository-component]]
            [spanner-test.app.worker.example :refer [example-worker-component]])
  (:gen-class))

(defn spanner-test-system
  [{:keys [spanner-test-project-id
           spanner-test-instance-id
           spanner-test-database-id]
    :as config-options}]
  (component/system-map
    :spanner (spanner-component
               spanner-test-project-id
               spanner-test-instance-id
               spanner-test-database-id)
    :user-repository (component/using
                       (user-repository-component)
                       [:spanner])
    :example-worker (component/using
                      (example-worker-component)
                      [:user-repository])))

(defn load-config []
  {:spanner-test-project-id (env :spanner-test-project-id)
   :spanner-test-instance-id (env :spanner-test-instance-id)
   :spanner-test-database-id (env :spanner-test-database-id)})

(defn -main []
  (component/start
    (spanner-test-system (load-config))))
