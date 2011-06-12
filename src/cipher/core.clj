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

(def deck (into (vec (range 1 53)) [:a :b]))

(defn move-down [deck card]
  (let [pos (.indexOf deck card)]
    (if (= (dec (count deck)) pos)
      (let [top (first deck)]
        (into [top card] (butlast (rest deck))))
      (let [target (get deck (inc pos))]
        (-> deck
            (assoc pos target)
            (assoc (inc pos) card))))))

(defn triple-cut [deck]
  (let [[top j1 mid j2 bottom] (partition-by #{:a :b} deck)]
    (vec (concat bottom j1 mid j2 top))))

(defn bottom-cut [deck]
  (let [bottom (peek deck)]
    (if (#{:a :b} bottom)
      deck
      (let [top-cards (take bottom deck)
            mid-cards (butlast (drop bottom deck))]
        (vec (concat mid-cards top-cards [bottom]))))))

(defn card->letter [card]
  (char (+ (if (< 26 card)
             (- card 26)
             card)
           64)))

(defn key-stream []
  (letfn [(deck-step [[deck _]]
            (let [new-deck (-> deck
                               (move-down :a)
                               (move-down :b)
                               (move-down :b)
                               triple-cut
                               bottom-cut)
                  top (first new-deck)
                  card (new-deck (if (#{:a :b} top) 53 top))]
              [new-deck card]))]
    (->> (iterate deck-step [deck nil])
         rest
         (map second)
         (remove #{:a :b})
         (map card->letter)
         (partition 5)
         (map join))))

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
        enum-ks (enumerate (key-stream))]
    (join " " (denumerate (combine enum-msg enum-ks)))))
