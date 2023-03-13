(ns swing-gd.core
  (:gen-class)
  (:import [javax.swing JFrame JPanel]
           [java.awt Color Graphics Graphics2D]))

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

(defn run []
  (let [panel (create-panel)
        frame (JFrame. "graphic dsl")]
    (doto frame
      (.add panel)
      (.setSize 640 400)
      (.setVisible true))))

(defn -main [& args]
  (run))

(comment
  (run)
  )
