(ns swing-gd.dsl
  (:require [clojure.string :as str]))

(defn parse-command [s]
  (let [[cmd arg] (str/split s #"\s+")]
    (if arg
      [cmd (Integer/parseInt arg)]
      [cmd])))

(defn parse [s]
  (->> (str/split-lines s)
       (map #(str/trim (str/replace-first % #"#.*$" "")))
       (filter (complement str/blank?))
       (map parse-command)))

(def moves {"W" [1 0], "S" [0 1], "E" [-1 0], "N" [0 -1]})
(defn move? [dir] (contains? moves dir))
(defn move [dir n pos]
  (->> (get moves dir)
       (map #(* % n))
       (map + pos)))

(defn move-st [dir n {pos :pos, state :state, width :width, res :res, :as st}]
  (let [new-pos (move dir n pos)
        new-res (if (= state :up) res
                    (conj res [pos new-pos width]))]
    (assoc st, :pos new-pos, :res new-res)))

(defn exec-one [st [cmd arg]]
  (case cmd
    "D" (assoc st :state :down)
    "U" (assoc st :state :up)
    "P" (assoc st :width arg)
    (if (move? cmd)
      (move-st cmd arg st))))

(defn exec [cmds]
  (let [init {:pos [0 0], :state :up, :width 1, :res []}]
    (:res (reduce exec-one init cmds))))
