(ns spanner-test.infra.datastore.spanner
  (:import (com.google.cloud.spanner DatabaseClient
                                     DatabaseId
                                     ResultSet
                                     Spanner
                                     SpannerOptions
                                     Statement))
  (:require [com.stuartsierra.component :as component]))


(defrecord SpannerComponent [project-id instance-id database-id
                             options spanner dbcli]
  component/Lifecycle
  (start [this]
    (println ";; Starting SpannerComponent")
    (let [opts (-> (SpannerOptions/newBuilder) .build)
          spanner (-> opts .getService)]
      (-> this
          (assoc :options opts)
          (assoc :spanner spanner)
          (assoc :dbcli (-> spanner (.getDatabaseClient
                                      (DatabaseId/of
                                        project-id
                                        instance-id
                                        database-id)))))))
  (stop [this]
    (println ";; Stopping SpannerComponent")
    (-> this :spanner .closeAsync)
    (-> this
        (dissoc :spanner)
        (dissoc :options))))

(defn spanner-component
  [project-id instance-id database-id]
  (map->SpannerComponent {:project-id project-id
                          :instance-id instance-id
                          :database-id database-id}))
