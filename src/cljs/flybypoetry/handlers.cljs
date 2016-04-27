(ns flybypoetry.handlers
  (:require-macros [cljs.core.async.macros :refer [go]])
    (:require [re-frame.core :as re-frame]
              [flybypoetry.db :as db]
              [cljs-http.client :as http]
              [cljs.core.async :refer [<!]]))

(re-frame/register-handler
 :initialize-db
 (fn  [_ _]
   db/default-db))

(re-frame/register-handler
 :set-active-panel
 (fn [db [_ active-panel]]
   (assoc db :active-panel active-panel)))


(re-frame/register-handler
 :process-magnets
 (fn [db [_ response]]
   (assoc db :magnets response)))

(re-frame/register-handler
 :load-magnets
 (fn [db _]
   (go (let [magnets (<! (http/get "/api/courses"))]
         (re-frame/dispatch [:process-magnets (-> magnets :body)])))
   db))
