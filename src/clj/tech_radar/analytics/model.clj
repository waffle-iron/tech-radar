(ns tech-radar.analytics.model
  (:require [tech-radar.analytics.protocols :refer [Analyze
                                                    Storage
                                                    Tweet]]))

(defn- add-topic-fn [tweet-info hashtags]
  (fn [data topic]
    (let [data* (update-in data [topic :texts] (fnil #(conj % tweet-info) []))]
      (update-in data* [topic :hashtags] (fn [values]
                                           (let [values* (or values {})]
                                             (reduce (fn [acc v]
                                                       (update-in acc [v] (fnil inc 0))) values* hashtags)))))))

(defn- last-texts [texts max-count]
  (let [count* (count texts)
        start  (if (> count* max-count)
                 (- count* max-count)
                 0)]
    (subvec texts start)))

(defn- init* [data initial-data]
  (reset! data initial-data))

(defn- add* [data tweet]
  (swap! data (fn [data {:keys [topics hashtags] :as tweet}]
                (let [tweet-info (select-keys tweet [:id :text :created-at :hashtags])]
                  (reduce (add-topic-fn tweet-info hashtags) data topics))) tweet))

(defn- trends* [data]
  (->> @data
       (map (fn [[topic {hashtags :hashtags}]]
              [topic hashtags]))
       (into {})))

(defn- texts* [data topic settings]
  (let [texts (or (get-in @data [(keyword topic) :texts])
                  [])
        {:keys [max-texts-per-request]} settings]
    (->> (last-texts texts max-texts-per-request)
         (map #(select-keys % [:id :text :created-at])))))

(defrecord Model [data settings]
  Storage
  (init [this initial-data]
    (init* (:data this) initial-data)
    nil)
  Tweet
  (add [this tweet]
    (add* (:data this) tweet)
    nil)
  Analyze
  (trends [this]
    (trends* (:data this)))
  (topic [this topic]
    (let [{:keys [data settings]} this]
      (texts* data topic settings))))

(defn new-model [settings]
  (let [{:keys [max-texts-per-request]} settings]
    (when-not max-texts-per-request
      (throw (Exception. "you have to provide max-texts-per-request param")))

    (map->Model {:data     (atom {})
                 :settings settings})))