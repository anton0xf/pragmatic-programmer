(ns swing-gd.core
  (:gen-class)
  (:require [swing-gd.dsl :refer [parse exec]])
  (:import [javax.swing JFrame JPanel JComponent]
           [java.awt Color Graphics Graphics2D BasicStroke]
           [java.util Timer TimerTask]))

(def segments-ex [[[0 0] [1 1] 2]
                  [[1 0] [0 1] 3]])

(def segments-ex-dsl (exec (parse "P 2\nD\nW 2\nN 1\nE 2\nS 1\nU")))

(defn point-scale [k p] (map * k p))
(defn point-shift [shift p] (map + shift p))

(defn update-segment-ps [f [p1 p2 sw]]
  [(f p1) (f p2) sw])

(defn point-affine-f [from-dim to-dim]
  (let [[fp f-size] from-dim
        [tp t-size] to-dim
        scale (map / t-size f-size)
        shift (map - tp (point-scale scale fp))]
    (fn [p] (->> p (point-scale scale) (point-shift shift) ))))

(defn point-add-border-f [width size]
  (let [shift [width width]
        scale (map #(- % (* 2 width)) size)]
    (fn [p] (->> p (point-scale scale) (point-shift shift)))))

(defn point-add-border-f [width size]
  (point-affine-f [[0 0] size]
                  [[width width]
                   (map #(- % (* 2 width)) size)]))

(defn segments-dimensions [segments] ;; => [[corner-x corner-y] [width heigh]]
  (let [ps (->> segments
                (map #(take 2 %))
                (reduce concat '()))
        xs (map first ps)
        ys (map second ps)
        x-min (reduce min xs), x-max (reduce max xs)
        y-min (reduce min ys), y-max (reduce max ys)]
    [[x-min y-min] [(- x-max x-min) (- y-max y-min)]]))


(defn draw-segment [#^Graphics g [x1 y1] [x2 y2] sw]
  (doto g
    (.setStroke (BasicStroke. sw))
    (.drawLine x1 y1 x2 y2)))

(defn render [#^Graphics g w h]
  (.setColor g (Color/BLUE))
  (let [segments (concat segments-ex segments-ex-dsl)
        dim (segments-dimensions segments)
        segments2 (map (partial update-segment-ps
                                (comp (point-add-border-f 50 [w h])
                                      (point-affine-f dim [[0 0] [w h]])
                                      ))
                       segments)]
    (doseq [[p1 p2 sw] segments2]
      (draw-segment g p1 p2 sw))))

(defn create-panel []
  "Create a panel with a customised render"
  (proxy [JPanel] []
    (paintComponent [g]
      (proxy-super paintComponent g)
      (render g (. this getWidth) (. this getHeight)))))

(defn repaint-task [#^JComponent component]
  (proxy [TimerTask] []
    (run [] (.repaint component))))

(defn repaint-timer [#^JComponent component delay]
  (let [timer (Timer. "repaint timer")
        task (repaint-task component)]
    (.schedule timer task delay delay)))

(defn run [autorefresh]
  (let [panel (create-panel)
        frame (JFrame. "graphic dsl")]
    (if autorefresh (repaint-timer panel 1000))
    (doto frame
      (.add panel)
      (.setSize 640 400)
      (.setVisible true))))

(defn -main [& args]
  (run false))
(comment (run true))
