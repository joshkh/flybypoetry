(ns flybypoetry.core
    (:require [reagent.core :as reagent]
              [re-frame.core :as re-frame]
              [flybypoetry.handlers]
              [flybypoetry.subs]
              [flybypoetry.routes :as routes]
              [flybypoetry.views :as views]
              [flybypoetry.config :as config]))

(when config/debug?
  (println "dev mode"))

(defn mount-root []
  (reagent/render [views/main-panel]
                  (.getElementById js/document "app")))

(defn ^:export init []
  (routes/app-routes)
  (re-frame/dispatch-sync [:initialize-db])
  (re-frame/dispatch [:load-magnets])
  (mount-root))
