(ns swing-gd.core
  (:gen-class)
  (:import [javax.swing JFrame JPanel JComponent]
           [java.awt Color Graphics Graphics2D BasicStroke]
           [java.util Timer TimerTask]))

(defn draw-segment [#^Graphics g [x1 y1] [x2 y2] sw]
  (doto g
    (.setStroke (BasicStroke. sw))
    (.drawLine x1 y1 x2 y2)))

(defn scale-p [p p0 s]
  (->> (map * p s)
       (map + p0)))

(defn scale-segments [segments w h d]
  (let [p0 [d d]
        s [(/ (- w (* 2 d)) w)
           (/ (- h (* 2 d)) h)]]
    (map (fn [[p1 p2 sw]] [(scale-p p1 p0 s)
                           (scale-p p2 p0 s)
                           sw])
         segments)))

(defn render [#^Graphics g w h]
  (.setColor g (Color/BLUE))
  (let [segments [[[0 0] [w h] 2]
                  [[w 0] [0 h] 3]]]
    (doseq [[p1 p2 sw] (scale-segments segments w h 50)]
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
