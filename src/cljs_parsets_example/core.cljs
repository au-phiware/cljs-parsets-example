(ns cljs-parsets-example.core
  (:require
   #_[om.core :as om :include-macros true]
   [cljsjs.d3]
   [d3.parsets]
   [reagent.core :as r]
   [sablono.core :as sab :include-macros true])
  (:require-macros
   [devcards.core :as dc :refer [defcard-rg defcard deftest]]))

(enable-console-print!)

(defn dom [_]
  [:div])

(defcard static
  (fn [state _]
    (.log js/console #js ["a" "b" "c"])
    (-> @state dom r/as-element))
  {})

(defn parasets [state]
  (letfn [(join-data* [root {:keys [data]}]
            (let [chart (-> js/d3 .parsets
                            (.dimensions #js ["a" "b" "c"]))
                  data (clj->js data)
                  _ (-> js/d3
                        (.select root)
                        (.append "svg")
                        (.attr "width" (.width chart))
                        (.attr "height" (.height chart))
                        (.datum data)
                        (.call chart))]))
          (join-data [component]
            (-> component r/dom-node (join-data* @state)))]
    (r/create-class
      {:display-name "parasets"
       :reagent-render (fn [] (dom @state))
       :component-did-mount join-data
       :component-did-update join-data})))

(defn random-datum []
  (hash-map
    "a" ([:non :low :med :high :emerg] (rand-int 5))
    "b" ([:mac :win] (rand-int 2))
    "c" ([:blue :red :violet :black :white] (rand-int 5))))

(defn random-data
  ([] (random-data (rand-int 100)))
  ([n] (repeatedly n random-datum)))

(defonce ratom (r/atom {:data (random-data)}))

(defcard-rg parasets-card
  [:div
   [parasets ratom]]
  ratom
  {:inspect-data true
   :history true})

(defn main []
  ;; conditionally start the app based on whether the #main-app-area
  ;; node is on the page
  (if-let [node (.getElementById js/document "main-app-area")]
    (.render js/ReactDOM (sab/html [:div "This is working"]) node)))

(main)

;; remember to run lein figwheel and then browse to
;; http://localhost:3449/cards.html

