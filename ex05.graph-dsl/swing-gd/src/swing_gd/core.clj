(ns swing-gd.core
  (:gen-class)
  (:import [javax.swing JFrame JPanel JComponent]
           [java.awt Color Graphics Graphics2D]
           [java.util Timer TimerTask]))

(defn render [#^Graphics g w h]
  (doto g
    (.setColor (Color/GREEN))
    (.drawLine 0 0 w h)
    (.drawLine w 0 0 h)))

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

(defn run []
  (let [panel (create-panel)
        timer (repaint-timer panel 1000)
        frame (JFrame. "graphic dsl")]
    (doto frame
      (.add panel)
      (.setSize 640 400)
      (.setVisible true))))

(defn -main [& args]
  (run))
