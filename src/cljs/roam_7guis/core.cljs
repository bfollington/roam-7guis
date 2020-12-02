(ns roam-7guis.core
  (:require
   [reagent.core :as reagent :refer [atom]]
   [reagent.dom :as rdom]
   [reagent.session :as session]
   [reitit.frontend :as reitit]
   [clerk.core :as clerk]
   [accountant.core :as accountant]
   [roam-7guis.counter :as counter]
   [roam-7guis.tempconv :as temp]
   [roam-7guis.flight-booker :as flight]
   [roam-7guis.timer :as timer]
   [roam-7guis.crud :as crud]
   [roam-7guis.circles :as circles]
   [roam-7guis.spreadsheet :as spreadsheet]))

;; -------------------------
;; Routes

(def router
  (reitit/router
   [["/" :index]]))

(defn path-for [route & [params]]
  (if params
    (:path (reitit/match-by-name router route params))
    (:path (reitit/match-by-name router route))))

;; -------------------------
;; Page components

(defn home-page []
  (fn []
    [:span.main
     [:h1 "Welcome to roam-7guis"]
     [:hr]
     [:h2 "1: counter"]
     [:div [counter/counter]]
     [:hr]
     [:h2 "2: temperature converter"]
     [:div [temp/temp-conv]]
     [:hr]
     [:h2 "3: flight booker"]
     [:div [flight/flight-booker]]
     [:hr]
     [:h2 "4: timer"]
     [:div [timer/timer]]
     [:hr]
     [:h2 "5: crud"]
     [:div [crud/crud]]
     [:hr]
     [:h2 "6: circles"]
     [:div [circles/circles]]
     [:hr]
     [:h2 "7: spreadsheet"]
     [:div [spreadsheet/spreadsheet]]]))

;; -------------------------
;; Translate routes -> page components

(defn page-for [route]
  (case route
    :index #'home-page))


;; -------------------------
;; Page mounting component

(defn current-page []
  (fn []
    (let [page (:current-page (session/get :route))]
      [:div
       [:header
        [:p [:a {:href (path-for :index)} "Home"] " | "
         [:a {:href (path-for :about)} "About roam-7guis"]]]
       [page]
       [:footer
        [:p "roam-7guis was generated by the "
         [:a {:href "https://github.com/reagent-project/reagent-template"} "Reagent Template"] "."]]])))

;; -------------------------
;; Initialize app

(defn mount-root []
  (rdom/render [current-page] (.getElementById js/document "app")))

(defn init! []
  (clerk/initialize!)
  (accountant/configure-navigation!
   {:nav-handler
    (fn [path]
      (let [match (reitit/match-by-path router path)
            current-page (:name (:data  match))
            route-params (:path-params match)]
        (reagent/after-render clerk/after-render!)
        (session/put! :route {:current-page (page-for current-page)
                              :route-params route-params})
        (clerk/navigate-page! path)))
    :path-exists?
    (fn [path]
      (boolean (reitit/match-by-path router path)))})
  (accountant/dispatch-current!)
  (mount-root))
