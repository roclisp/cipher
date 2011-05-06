(ns cipher.test.core
  (:use [cipher.core] :reload)
  (:use [clojure.test]))

(def msg1 "Code in Ruby, live longer!")

(deftest test-step-1
  (is (= ["CODEI" "NRUBY" "LIVEL" "ONGER"] (step-1 msg1)))
  (is (= ["ABCDE" "FXXXX"] (step-1 "ABCDEF"))))