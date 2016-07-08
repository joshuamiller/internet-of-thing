(ns ui.core
  (:require [reagent.core :as reagent :refer [atom]]
            [re-com.core :refer [v-box h-box title gap label]]
            [cljs.core.async :refer [<!]]
            [cljs.reader :refer [read-string]]
            [clojure.string :as string :refer [split-lines]]
            [ui.sqs :as sqs])
   (:require-macros [cljs.core.async.macros :refer [go go-loop]]))


;;; Just getting basic connection info from the environment.

(defn env
  [attr]
  (aget (.-env js/process) attr))

(def queue-url (env "SQS_URL"))


;;; Sometimes we will want to talk to the main process

(defonce app (.-app (.-remote (js/require "electron"))))


;;; Like to set our app's name

(.setName app "Internet of Thing")


;;; When things break, we can just print to the developer console

(enable-console-print!)


;;; Our state is just a vector of IoT button messages

(defonce state (atom []))


;;; When we receive a message, we're going to conj it to the state
;;; And we're going to set the badge
;;; And we're going to bounce our dock icon

(defn receive-message
  [msg]
  (swap! state conj (-> msg
                        (aget "Body")
                        (read-string)))
  (.setBadgeCount app (count @state))
  (.bounce (.-dock app)))

(def consumer (sqs/consumer queue-url receive-message))


;;; We're using re-com components to build our UI

(defn root-component []
  [v-box
   :height "100%"
   :width "100%"
   :padding "15px"
   :children
   [
    [title
     :label "Internet Of Thing"
     :level :level1]
    [gap :size "50px"]
    [h-box
     :padding "15px"
     :children
     [
      [label
       :label (str "Received " (count @state) " messages")]
      [gap :size "50px"]
      [:code
       (str "Messages: " @state)]]]]])


;;; Now that that's all set up, let's actually render the UI

(reagent/render
  [root-component]
  (.-body js/document))


;;; And start our SQS consumer

(.start consumer)







;;; Some fun stuff

(comment

  ; Our hook into the remote process
  (def remote (.-remote (js/require "electron")))

  (let [native-image (.-nativeImage remote)
        image (.createFromPath native-image "/Users/josh/projects/internet-of-thing/AWS_IoT_button_short.jpg")]
    (.setIcon (.-dock app) image)
    (.getSize image))

  (.hide (.-dock app))

  (.show (.-dock app))

  (.setBadge (.-dock app) "IoT")

  (.setBadge (.-dock app) "")

  ; Write the last batteryVoltage to the clipboard
  (let [clipboard (.-clipboard remote)]
    (.writeText clipboard (get (last @state) "batteryVoltage")))

  ; Register a global key shortcut
  (let [global-shortcut (.-globalShortcut remote)]
    (.register global-shortcut "Command+Option+x" #(print "Pressed!")))

  ; Unregister shortcuts
  (let [global-shortcut (.-globalShortcut remote)]
    (.unregisterAll global-shortcut))

  ; Open a file dialog
  (let [dialog (.-dialog remote)]
    (.showOpenDialog dialog #js{:defaultPath "/Users/josh/projects/internet-of-thing/"}))

  ; Save the contents of our current state to a file
  (let [dialog (.-dialog remote)
        fs (js/require "fs")]
    (.showSaveDialog dialog
                     #js{:defaultPath "/Users/josh/projects/internet-of-thing/"}
                     (fn [filename]
                       (.writeFile fs filename @state))))

  (let [dialog (.-dialog remote)]
    (.showErrorBox dialog "Warning!" "This Thing is on the Internet!"))

  ; Add a right-clickable context menu
  (let [menu (.-Menu remote)
        menu-item (.-MenuItem remote)
        new-menu (menu.)]
    (.append new-menu (menu-item. #js{:label "Click Me"
                                      :click #(print "Clicked!")}))
    (.addEventListener js/window "contextmenu"
                       (fn [e]
                         (.preventDefault e)
                         (.popup new-menu (.getCurrentWindow remote))) false))

  
  )
