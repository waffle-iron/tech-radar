(ns tech-radar.state)

(defonce app-state (atom {:current-screen :trends
                          :msg            "Hello Om.Next world!"
                          :menu-items     [{:id   :jobs
                                            :href "#/topic/jobs"
                                            :name "Jobs"}
                                           {:id   :clojure
                                            :href "#/topic/clojure"
                                            :name "Clojure"}
                                           {:id   :jvm
                                            :href "#/topic/jvm"
                                            :name "JVM"}
                                           {:id   :javascript
                                            :href "#/topic/javascript"
                                            :name "JavaScript"}
                                           {:id   :golang
                                            :href "#/topic/golang"
                                            :name "Golang"}
                                           {:id   :linux
                                            :href "#/topic/linux"
                                            :name "Linux"
                                            :svg  :linux}
                                           {:id   :nosql
                                            :href "#/topic/nosql"
                                            :name "NoSQL"}]
                          :trends         {}
                          :topics         {}}))