(ns roam-7guis.parser
  (:require [roam-7guis.util :as u]))

(defn find-deps [[op arg] deps]
  (let [arg-list-deps (reduce concat (map #(find-deps % []) arg))]
    (case op
      :formula (find-deps arg deps)
      :ref (conj deps arg)
      :add arg-list-deps
      :sub arg-list-deps
      :mul arg-list-deps
      :div arg-list-deps

      (conj deps (str op arg)))))

(defn evaluate [get-cell-value [op arg]]
  (u/log [op arg])
  (let [eval-operator (fn [op]
                        (reduce op (map
                                    (comp js/parseInt (partial evaluate get-cell-value))
                                    arg)))]
    (case op
      :value arg
      :formula (evaluate get-cell-value arg)
      :ref (get-cell-value arg)
      :add (eval-operator +)
      :sub (eval-operator -)
      :mul (eval-operator *)
      :div (eval-operator /)

    ;; should really be validating that this is a real cell ID
      (get-cell-value (str op arg)))))

(defn evaluate-deps [formula]
  (try
    (let [parsed (cljs.reader/read-string formula)
          deps (find-deps parsed [])]
      deps)
    (catch js/Object _
      [])))

(defn evaluate-formula [formula get-cell-value]
  (try
    (let [parsed (cljs.reader/read-string formula)
          result (evaluate get-cell-value [:formula parsed])
          deps (find-deps parsed [])]

      (u/log ["evaluate-formula" result deps])
      result)
    (catch js/Object _
      "#ERROR!")))
