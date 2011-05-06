(ns cipher.core)

(defn- join [coll]
  (apply str coll))

(defn- pad [s n filler]
  (if (> n (count s))
    (str s (join (repeat (- n (count s)) filler)))
    s))

(defn step-1 [msg]
  (let [msg2 (-> msg
                 .toUpperCase
                 (.replaceAll "[^A-Z]" ""))
        parts (map join (partition-all 5 msg2))]
    (concat
     (butlast parts)
     [(pad (last parts) 5 "X")])))


