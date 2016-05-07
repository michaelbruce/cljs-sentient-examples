(ns cljs-sentient-examples.core
  (:require [reagent.core :as reagent :refer [atom]]))

(enable-console-print!)

; TODO css up this bitch, prettify pls.
; TODO clear up code

(defonce app-state
  (atom {:x ""
         :y ""
         :z 72}))

(defn machine-code []
  (. js/Sentient
     (compile
      (str
       "int x, y;"
       ""
       "x_power_3 = x * x * x;"
       "y_power_2 = y * y;"
       ""
       "z = x_power_3 + y_power_2;"
       "z += x;"
       "z -= y;"
       ""
       "vary x, y, z;"))))

(defn read-from-input [name]
  (let [value (js/parseInt (name @app-state))]
    (if (js/isNaN value)
      :no-value value)))

(defn assign [collection name value]
  (if-not (js/isNaN value)
    (aset collection name value)))

(defn jsx->clj
  [x]
  (into {} (for [k (.keys js/Object x)] [(keyword k) (aget x k)])))
  
(defn sentient []
  (do
    (def args (js-obj))
    (assign args "x" (read-from-input :x))
    (assign args "y" (read-from-input :y))
    (assign args "z" (read-from-input :z))
    (println args)
    (let [result (. js/Sentient (run
                                  (machine-code)
                                  args))]
      (reset! app-state (jsx->clj result))
      (println result))))

(defn input [value]
    [:input {:type "text"
             :placeholder (name value)
             :value (value @app-state)
             :class "style-1"
             :size 5
             :on-change #(do (swap! app-state assoc value (-> % .-target .-value))
                             (sentient))}])

(defn interface []
  [:div
   [:h1 "My first Sentient"]
   [:p (input :x) "^3 + " (input :y) "^2 + " (input :x) " - " (input :y) " = " (input :z)]])

(reagent/render-component [interface]
                          (. js/document (getElementById "app")))

(defn on-js-reload [] (println "Reloaded!"))