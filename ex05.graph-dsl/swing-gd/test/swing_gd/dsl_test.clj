(ns swing-gd.dsl-test
  (:require
   [clojure.test :refer :all]
   [swing-gd.dsl :refer :all]))

(deftest test-parse
  (is (= [] (parse "")))
  (is (= [["D"]] (parse "D")))
  (is (= [["D"]] (parse "D ")))
  (is (= [["D"]] (parse "\nD\n\n")))
  (is (= [["D"]] (parse "D\n # comment")))
  (is (= [["D"] ["U"]] (parse "D\nU")))
  (is (= [["D"] ["W" 2]] (parse "D#comment\nW 2 # other comment")))
  (is (= [["W" 2]] (parse "W  2"))))

(deftest test-exec
  (is (= [[[0 0] [2 0] 1]] (exec [["D"] ["W" 2]])))
  (is (= [[[0 0] [2 0] 2]] (exec (parse "P 2\nD\nW 2"))))
  (is (= [[[0 1] [2 1] 1]] (exec [["S" 1] ["D"] ["W" 2]])))
  (is (= [[[0 0] [2 0] 1] [[2 1] [1 1] 1]]
         (exec (parse "D\nW 2\nU\nS 1\nD\nE 1")))))
