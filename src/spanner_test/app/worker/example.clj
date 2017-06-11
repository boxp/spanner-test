(ns spanner-test.app.worker.example
  (:require [com.stuartsierra.component :as component]
            [clojure.core.async :refer [go put! <! timeout chan alt!]]
            [spanner-test.infra.repository.user :refer [example dummy-user add-user]]))

(def interval 10)

(defn worker
  [this cancel-chan]
  (go
    (let [t (timeout interval)]
      (loop [in :timeout]
             (when (not= :cancel in)
               (-> this :user-repository example)
               (-> this :user-repository (add-user (dummy-user)))
               (recur (alt! cancel-chan (fn [res] res)
                            ;;t :timeout
                            )))))))

(defrecord ExampleWorkerComponent [user-repository cancel-chans]
  component/Lifecycle
  (start [this]
    (let [cancel-chans (->> (repeatedly #(chan)) (take 10))]
      (println ";; Starting ExampleWorkerComponent")
      (doseq [c cancel-chans]
        (worker this c))
      (-> this
          (assoc :cancel-chans cancel-chans))))
  (stop [this]
    (println ";; Stopping ExampleWorkerComponent")
    (doseq [c (:cancel-chans this)]
      (put! c :cancel))
    (-> this
        (dissoc :cancel-chans)
        (dissoc :user-repository))))

(defn example-worker-component []
  (map->ExampleWorkerComponent {}))
