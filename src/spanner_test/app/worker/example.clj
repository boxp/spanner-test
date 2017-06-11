(ns spanner-test.app.worker.example
  (:require [com.stuartsierra.component :as component]
            [clojure.core.async :refer [go put! <! timeout chan alt!]]
            [spanner-test.infra.repository.user :refer [example dummy-user add-user]]))

(def interval 10)

(defrecord ExampleWorkerComponent [user-repository chancel-chan]
  component/Lifecycle
  (start [this]
    (println ";; Starting ExampleWorkerComponent")
    (go
      (let [chancel-chan (chan)
            t (timeout interval)]
        (loop [in :timeout]
               (when (not= :cancel in)
                 (-> this :user-repository example)
                 (-> this :user-repository (add-user (dummy-user)))
                 (recur (alt! chancel-chan (fn [res] res)
                              t :timeout))))))
    (-> this
        (assoc :chancel-chan chancel-chan)))
  (stop [this]
    (println ";; Stopping ExampleWorkerComponent")
    (put! (:chancel-chan this) :cancel)
    (-> this
        (dissoc :chan)
        (dissoc :user-repository))))

(defn example-worker-component []
  (map->ExampleWorkerComponent {}))
