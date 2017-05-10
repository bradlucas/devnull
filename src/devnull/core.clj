(ns devnull.core
  (:require [org.httpkit.server :as httpd]))

(defonce server (atom nil))

(defn stop-server []
  (when-not (nil? @server)
    ;; graceful shutdown: wait 100ms for existing requests to be finished
    ;; :timeout is optional, when no timeout, stop immediately
    (println "Stopping server")
    (@server :timeout 100)
    (reset! server nil)))

;; http://www.http-kit.org/server.html

(defn app [req]
  {:status 200
   :headers {"Context-Type" "text/html"}
   :body "OK"})

(defn start-server []
  ;; The #' is useful when you want to hot-reload code
  ;; You may want to take a look: https://github.com/clojure/tools.namespace
  ;; and http://http-kit.org/migration.html#reload
  (reset! server (httpd/run-server #'app {:port 8080})))

(defn shutdown-hook
  [fn]
  ;; setup fn to run when the app is shutdown
  (let [shutdown-thread (new Thread fn)]
    (.. Runtime (getRuntime) (addShutdownHook shutdown-thread))))

(defn -main [& args]
  (shutdown-hook stop-server)
  (println "Starting server")
  (start-server))

