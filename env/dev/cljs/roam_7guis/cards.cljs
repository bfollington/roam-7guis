(ns roam-7guis.cards
  (:require [reagent.core :as reagent :refer [atom]]
            [reagent.dom :as rdom]
            [roam-7guis.core :as core]
            [devcards.core :as dc]
            [roam-7guis.counter :as counter]
            [roam-7guis.tempconv :as temp]
            [roam-7guis.flight-booker :as flight]
            [roam-7guis.timer :as timer]
            [roam-7guis.crud :as crud]
            [roam-7guis.circles :as circles]
            [roam-7guis.spreadsheet :as spreadsheet]
            [roam-7guis.bullet-journal :as journal])
  (:require-macros
   [devcards.core
    :as dc
    :refer [defcard defcard-doc defcard-rg deftest]]))

(defcard-rg counter
  (counter/counter))

(defcard-rg temp-conv
  (temp/temp-conv))

(defcard-rg flight-booker
  (flight/flight-booker))

(defcard-rg timer
  (timer/timer))

(defcard-rg crud
  (crud/crud))

(defcard-rg circles
  (circles/circles))

(defcard-rg spreadsheet
  (spreadsheet/spreadsheet))

(defcard-rg bullet-journal
  (journal/journal))

(rdom/render [:div] (.getElementById js/document "app"))

;; remember to run 'lein figwheel devcards' and then browse to
;; http://localhost:3449/cards
