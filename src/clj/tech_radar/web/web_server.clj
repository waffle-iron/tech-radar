(ns tech-radar.web.web-server
  (:require [bidi.bidi :as bidi]
            [bidi.ring :as bidi-ring]
            [tech-radar.web.resources :refer [trends-resource
                                              topic-resource
                                              search-resource]]
            [taoensso.timbre :as timbre]))

(defn- create-resources [analysis]
  {:trends (trends-resource analysis)
   :topics (topic-resource analysis)
   :search (search-resource analysis)})

(def get-routes {"trends"           :trends
                 ["topics/" :topic] :topics})

(def post-routes {["search/" :topic] :search})

(def site-routes ["/" {:get  get-routes
                       :post post-routes}])

(defn create-ring-handler [analysis]
  (let [resources  (create-resources analysis)
        route      (bidi/compile-route site-routes)
        handler-fn (fn [id]
                     (resources id))]
    (bidi-ring/make-handler route handler-fn)))

(defn allow-cross-origin
  [handler]
  (fn [request]
    (let [response (handler request)]
      (-> response
          (assoc-in [:headers "Access-Control-Allow-Origin"] "*")
          (assoc-in [:headers "Access-Control-Allow-Methods"] "GET,PUT,POST,DELETE,OPTIONS")
          (assoc-in [:headers "Access-Control-Allow-Headers"]
                    "X-Requested-With,Content-Type,Cache-Control,token")))))

(defn wrap-exception [f]
  (fn [request]
    (try (f request)
         (catch Exception e
           (timbre/error e)
           {:status 500
            :body   "Server error"}))))
