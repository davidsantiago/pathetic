#+clj (ns pathetic.test.core
        (:refer-clojure :exclude [resolve])
        (:use pathetic.core
              clojure.test))

#+cljs (ns pathetic.test.core
         (:refer-clojure :exclude [resolve])
         (:require-macros [cemerick.cljs.test :refer (is deftest with-test run-tests testing)])
         (:use [pathetic.core :only [parse-path render-path up-dir normalize url-normalize relativize resolve split-url-on-path ensure-trailing-separator]])
         (:require [cemerick.cljs.test :as t]))

(deftest test-parse-path
  (is (= nil (parse-path nil)))
  (is (= nil (parse-path "")))
  (is (= [:root] (parse-path "/")))
  (is (= [:root "A"] (parse-path "/A")))
  (is (= [:root "A" "B"] (parse-path "/A/B")))
  (is (= [:cwd] (parse-path ".")))
  (is (= [:cwd "A"] (parse-path "./A")))
  (is (= [:cwd "A" "B"] (parse-path "./A/B")))
  (is (= [:cwd "A"] (parse-path "A")))
  (is (= [:cwd "A" "B"] (parse-path "A/B")))
  (is (= [:cwd ".." "A"] (parse-path "../A"))))

(deftest test-render-path
  (is (= "/A" (render-path [:root "A"])))
  (is (= "/A/B" (render-path [:root "A" "B"])))
  (is (= "." (render-path [:cwd])))
  (is (= "A" (render-path [:cwd "A"])))
  (is (= "A/B" (render-path [:cwd "A" "B"])))
  (is (= ".." (render-path [:cwd ".."])))
  (is (= "../A" (render-path [:cwd ".." "A"]))))

(deftest test-up-dir
  (is (= [:cwd ".."] (up-dir [:cwd])))
  (is (= [:cwd] (up-dir [:cwd "A"])))
  (is (= [:root] (up-dir [:root "A"])))
  (is (= [:root] (up-dir [:root])))
  (is (= [:cwd "A"] (up-dir [:cwd "A" "B"])))
  (is (= [:root "A"] (up-dir [:root "A" "B"]))))

(deftest test-normalize
  (is (= "/A/B" (normalize "/A/B/C/..")))
  (is (= "/A/B" (normalize "/A/B/.")))
  (is (= "/A/B" (normalize "/A/B/C/../")))
  (is (= "/A/B" (normalize "/A/B/./")))
  (is (= "/A/B" (normalize "/A/C/../B/")))
  (is (= "A/B" (normalize "A/B")))
  (is (= "../A" (normalize "../A")))
  (is (= "." (normalize "A/..")))
  (is (= "A" (normalize "./A")))
  (is (= "A" (normalize "././A")))
  (is (= "." (normalize ".")))
  (is (= "." (normalize "./.")))
  (is (= "../.." (normalize "../.."))))

(deftest test-relativize
  (is (= "B"
         (relativize "/A" "/A/B")))
  (is (= "B"
         (relativize "/A" "/A/./B")))
  (is (= "B"
         (relativize "/A" "/A/B/")))
  (is (= ".."
         (relativize "/A" "/A/B/../..")))
  (is (= ".."
         (relativize "/A" "/A/B/../../")))
  (is (= "B"
         (relativize "A" "A/B")))
  (is (= "B"
         (relativize "A" "A/./B")))
  (is (= ".."
         (relativize "A" "A/B/../..")))
  (is (= "../E/F"
         (relativize "/A/B/C/D" "/A/B/C/E/F"))))

(deftest test-resolve
  (is (= "/A/B" (resolve "/A/" "B")))
  (is (= "/A/B" (resolve "/A" "B")))
  (is (= "/A" (resolve "/A" nil)))
  (is (= "/B" (resolve "/A" "/B")))
  (is (= "/B" (resolve "/A/" "/B")))
  (is (= "A/B" (resolve "A" "B")))
  (is (= "A/B" (resolve "A/" "B/"))))

;; In JDK7, java.nio.file.Path guarantees that if p and q are normalized paths,
;; and q does not start at root, then
;;      (= q (relativize p (resolve p q)))
;; Just a few sanity checks here.
(deftest test-relativize-resolve-sanity
  (is (= "B/C"
         (relativize "A" (resolve "A" "B/C"))))
  (is (= "B/C"
         (relativize "/A" (resolve "/A" "B/C"))))
  (is (= ".."
         (relativize "/A" (resolve "/A" ".."))))
  (is (= ".."
         (relativize "A" (resolve "A" ".."))))
  (is (= "../.."
         (relativize "A" (resolve "A" "../..")))))

(deftest test-ensure-trailing-separator
  (is (= "/A/B/" (ensure-trailing-separator "/A/B/")))
  (is (= "/A/B/" (ensure-trailing-separator "/A/B")))
  (is (= "A/B/" (ensure-trailing-separator "A/B/")))
  (is (= "A/B/" (ensure-trailing-separator "A/B")))
  (is (= "A/B/" (ensure-trailing-separator "A/B/")))
  (is (= "A/B/" (ensure-trailing-separator "A/B"))))

(deftest test-split-url-on-path
  (is (= ["http://a.b.c" "/d/e/f" "?g=h"]
           (split-url-on-path "http://a.b.c/d/e/f?g=h")))
  (is (= ["http://a.b.c" "/d/e/f" ""]
           (split-url-on-path "http://a.b.c/d/e/f")))
  (is (= ["http://a.b.c" "///d/e/f/" "?g=h"]
           (split-url-on-path "http://a.b.c///d/e/f/?g=h"))))

(deftest test-url-normalize
  (is (= "http://a.b.c/d/e/f?g=h" (url-normalize "http://a.b.c/d/e/f?g=h")))
  (is (= "http://a.b.c/d/e/f?g=h" (url-normalize "http://a.b.c///d/e/f?g=h")))
  (is (= "http://a.b.c/d/e/f?g=h#i" (url-normalize
                                     "http://a.b.c/d/e/f?g=h#i")))
  (is (= "http://a.b.c/d/e/f?g=h#i" (url-normalize
                                     "http://a.b.c/d/e/f/g/..?g=h#i")))
  (is (= "http://a.b.c/d/e/f?g=h#i" (url-normalize
                                     "http://a.b.c/d/e/f/g/../?g=h#i")))
  (is (= "http://don:dr4p3r@a.b.c:8080/d/e/f?g=h#i"
         (url-normalize "http://don:dr4p3r@a.b.c:8080/d/e/f?g=h#i")))
  (is (= "http://don:dr4p3r@a.b.c:8080/d/e/f?g=h#i"
         (url-normalize "http://don:dr4p3r@a.b.c:8080////d/e//f?g=h#i"))))
