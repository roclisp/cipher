(ns cipher.core)

(defn- join [coll]
  (apply str coll))

(defn- pad [s n filler]
  (if (> n (count s))
    (str s (join (repeat (- n (count s)) filler)))
    s))

(defn step-1 [msg]
  (let [bare (-> msg
                 .toUpperCase
                 (.replaceAll "[^A-Z]" ""))
        pad-len (* 5 (Math/ceil (/ (count bare) 5))) ;round up to nearest 5
        padded (pad bare pad-len \X)]
    (map join (partition 5 padded))))

