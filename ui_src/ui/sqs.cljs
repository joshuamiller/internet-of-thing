(ns ui.sqs)

(defonce sqs-consumer (js/require "sqs-consumer"))

(defn consumer
  [url handler]
  (.create sqs-consumer
           #js{:queueUrl url
               :handleMessage (fn [msg done] (handler msg) (done))}))
