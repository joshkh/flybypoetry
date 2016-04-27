(ns flybypoetry.main
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent]
            [dommy.core :as dommy :refer-macros [sel sel1]]))

(enable-console-print!)


(defn mousedown-handler [e]
  (println "DRAGGING")
  (.log js/console e))

  (defn mouseup-handler [e]
    (println "STOPPING"))

(defn canvas []
  (fn []
    [:div.canvas]))

(defn magnet []
  (let [pos (reagent/atom {:x 0 :y 0})]
    (reagent/create-class
     {:reagent-render (fn [val]
                        [:div.magnet
                         [:span (str (:symbol val) " (" (:pos val) ")")]])
      :component-did-mount
      (fn [e]
        (let [node (reagent/dom-node e)
              rand-rot (- (rand-int 20) 10)]
          (dommy/set-style! node :transform (str "rotate(" rand-rot "deg)"))
          (.draggable (js/$ node))))})))

(defn main []
  (let [magnets (re-frame/subscribe [:magnets])]
    (fn []
      [:div
       [canvas]
       (into [:div] (doall
                      (map (fn [m] [magnet m])
                           @magnets)))])))
