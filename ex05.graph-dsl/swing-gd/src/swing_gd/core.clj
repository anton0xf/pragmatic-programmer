(ns swing-gd.core
  (:gen-class)
  (:import [javax.swing JFrame JPanel JComponent]
           [java.awt Color Graphics Graphics2D BasicStroke]
           [java.util Timer TimerTask]))

(defn render [#^Graphics g w h]
  (.setColor g (Color/BLUE))
  (let [segments [[0 0 w h 2]
                  [w 0 0 h 3]]]
    (doseq [[x1 y1 x2 y2 sw] segments]
      (.setStroke g (BasicStroke. sw))
      (.drawLine g x1 y1 x2 y2))))

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
