(ns roam-7guis.flight-booker
  (:require [reagent.core :as reagent :refer [atom]]
            [clojure.pprint :as pp]
            [cljs-time.core :as time]
            [cljs-time.format :refer [parse unparse formatter]]
            [re-com.core :refer [h-box box gap v-box hyperlink-href p]]))

(defn value [e]
  (-> e .-target .-value))

(defn log [& args]
  (doseq [arg args]
    (pp/pprint arg)))

(defn set-state! [state key value]
  ;; (log @state key value)
  (swap! state #(assoc % key value)))

;;

(def date-format (formatter "dd/MM/YYYY"))

;;

(defn set-flight-type! [state type]
  (set-state! state :type type))

(defn flight-type [value state]
  [:select {:value value
            :on-change (fn [e] (let [value (-> e .-target .-value)] (set-flight-type! state value)))}
   [:option {:value :one-way} "one-way flight"]
   [:option {:value :return} "return flight"]])

(defn yesterday-at-midnight []
  (time/plus (time/today-at-midnight) (time/days -1)))

(defn validate-date [date-str]
  (let [date (try
               (parse date-format date-str)
               (catch :default _
                 :invalid))
        valid (if (not (= date :invalid))
                (time/after? date (time/today-at-midnight)) ;; dates must be in the future
                false)]
    {:value date-str :valid valid}))

(defn date-entry [field key state & {:keys [disabled] :or {disabled false}}]
  [:input {:value (:value field)
           :style {:background
                   (cond
                     (:valid field) "white"
                     disabled "#eee"
                     (not (:valid field)) "#FF9999")}
           :disabled disabled
           :on-change (fn [e]
                        (let [value (-> e .-target .-value)]
                          (set-state! state key (validate-date value))))}])

(defn is-one-way-flight? [state]
  (= (:type state) "one-way"))

(defn return-before-depart? [state]
  (if (and (-> state :depart-date :valid)
           (-> state :return-date :valid))
    (let [depart (->> state :depart-date :value (parse date-format))
          return (->> state :return-date :value (parse date-format))]
      (time/before? return depart))
    false))

(defn flight-booker []
  (let [state (atom {:type "one-way"
                     :depart-date {:value (unparse date-format (time/today-at-midnight))
                                   :valid true}
                     :return-date {:value (unparse date-format (time/today-at-midnight))
                                   :valid true}})]
    (fn []
      [v-box
       :width "256px"
       :children [[:p "Flights can only be booked in the future"]
                  (flight-type (:type @state) state)
                  (date-entry (-> @state :depart-date) :depart-date state)
                  (date-entry
                   (-> @state :return-date)
                   :return-date
                   state
                   :disabled (or (-> @state :depart-date :valid not)
                                 (return-before-depart? @state)
                                 (is-one-way-flight? @state)))
                  [:button "Book"]]])))