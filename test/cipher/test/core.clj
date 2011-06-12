(ns cipher.test.core
  (:use [cipher.core] :reload)
  (:use [clojure.test]))

(def msg-1 "Code in Ruby, live longer!")
(def msg-2 "abcdef")

(def normalized-1 ["CODEI" "NRUBY" "LIVEL" "ONGER"])
(def normalized-2 ["ABCDE" "FXXXX"])

(def enumerated-1 [[3 15 4 5 9] [14 18 21 2 25] [12 9 22 5 12] [15 14 7 5 18]])
(def enumerated-2 [[1 2 3 4 5] [6 24 24 24 24]])

(deftest test-normalize
  (is (= normalized-1 (normalize msg-1)))
  (is (= normalized-2 (normalize msg-2)))
  (is (empty? (normalize ""))))

(deftest test-enumerate
  (is (= enumerated-1 (enumerate normalized-1)))
  (is (= enumerated-2 (enumerate normalized-2)))
  (is (empty? (enumerate []))))

(deftest test-denumerate
  (is (= normalized-1 (denumerate enumerated-1)))
  (is (= normalized-2 (denumerate enumerated-2)))
  (is (empty? (denumerate []))))

(deftest test-card->letter
  (is (= (card->letter 1) \A))
  (is (= (card->letter 2) \B))
  (is (= (card->letter 26) \Z))
  (is (= (card->letter 27) \A))
  (is (= (card->letter 28) \B))
  (is (= (card->letter 52) \Z)))

(deftest test-move-down
  (is (= (move-down [1  2  :a 3  4] :a)
                    [1  2  3  :a 4]))

  (is (= (move-down [1  2  3  :a 4] :a)
                    [1  2  3  4  :a]))

  (is (= (move-down [1  2  3  4  :a] :a)
                    [1  :a 2  3  4])))
