(ns cipher.core
  (:use [clojure.string :only [join]]))


(defn- pad [s n filler]
  (if (> n (count s))
    (str s (join (repeat (- n (count s)) filler)))
    s))

(defn normalize [msg]
  (let [bare (-> msg
                 .toUpperCase
                 (.replaceAll "[^A-Z]" ""))
        pad-len (* 5 (Math/ceil (/ (count bare) 5))) ;round up to nearest 5
        padded (pad bare pad-len \X)]
    (map join (partition 5 padded))))

(defn key-stream [msg]
  msg)

(defn enumerate [msg]
  (for [group msg]
    (map #(- (int %) 64) group)))

(defn denumerate [enum-msg]
  (for [group enum-msg]
    (join (map #(char (+ % 64)) group))))

(defn combine [enum-msg enum-ks]
  (letfn [(add [a b]
            (let [result (+ a b)]
              (if (> result 26)
                (- result 26)
                result)))]
    (map (fn [g1 g2]
           (map add g1 g2))
         enum-msg enum-ks)))

(defn encode [msg]
  (let [norm-msg (normalize msg)
        enum-msg (enumerate norm-msg)
        enum-ks (enumerate (key-stream norm-msg))]
    (join " " (denumerate (combine enum-msg enum-ks)))))
