(ns pathetic.core
  (:refer-clojure :exclude [resolve])
  (:require [clojure.string :as str])
  (:import java.io.File))

(def ^{:private true} separator-pattern
  (re-pattern File/separator))

;; A note about the internal representation we work with for paths in this code.
;; ---
;; We work with vectors of path components, basically (that is, strings of the
;; pieces between File/separator). If the path is an absolute path, the first
;; component will be :root, so that if during processing everything else is
;; removed, we know to render "/" and not ".". Similarly, if the path is a
;; relative path, the first component will be :cwd.

;; Utility Functions

(defn- common-prefix
  "Given two collections, returns a sequence containing the prefix they
   share. Example: (common-prefix [\\a \\b] [\\a \\b \\c \\d]) -> (\\a \\b)"
  [coll1 coll2]
  (map first (take-while #(= (first %) (second %))
                         (map #(vector %1 %2) coll1 coll2))))

(defn- unique-suffix
  "Returns the elements of interesting-coll that are not part of the common
   prefix with uninteresting-coll."
  [uninteresting-coll interesting-coll]
  (let [common-parts (common-prefix uninteresting-coll interesting-coll)]
    (drop (count common-parts) interesting-coll)))

(defn parse-path
  "Given a string containing a relative or absolute path, returns the
   corresponding data structure described at the top of the file.

   This function does not do any normalization or simplification. However,
   because there is more than one way to write some paths, some simplification
   might happen anyways, such as if the path starts with a (redundant) \".\"."
  [path]
  (let [path-pieces (str/split (str path) separator-pattern)]
    ;; (str/split "/" #"/") => [], so we check for this case first.
    (if (= 0 (count path-pieces))
      [:root]
      (case (first path-pieces)
            ;; If first item is "", we split a path that started with "/".
            ;; Then we need to skip the "" at the start of path-pieces.
            "" (apply vector :root (rest path-pieces))
            ;; If the first item is ".", note that we start with :cwd and then
            ;; discard the ".".
            "." (apply vector :cwd (rest path-pieces))
            (apply vector :cwd path-pieces)))))

(defn render-path
  "Given a seq of path elements as created by parse-path, returns a string
   containing the path represented.

   NOTE: It is NOT the goal of this function to perform normalization, it just
   renders what it is given. HOWEVER, that does NOT mean that it is always true
   that (= (render-path (parse-path some-path)) some-path). That is, you may not
   render the exact same string you parsed. This is because the path syntax does
   not have exactly one way to write every path."
  [path-pieces]
  (case (first path-pieces)
        :root (str "/" (str/join File/separator (rest path-pieces)))
        :cwd (if (next path-pieces)
               (str/join File/separator (rest path-pieces))
               ".")
        (str/join File/separator path-pieces)))

(defn up-dir
  "Given a seq of path elements as created by parse-path, returns a new
   seq of path elements, but having gone \"up\" one directory. That is,
   applies a \"..\" component to the path."
  [path-pieces]
  (case (last path-pieces)
        ;; If the only thing in the path is :cwd, we reached the end of a
        ;; relative path, and need to add the ".." to keep track of the
        ;; intention for the relative path. Similarly, if the previous
        ;; part is a "..", then we should add another, instead of removing the
        ;; previous one.
        (:cwd "..") (conj path-pieces "..")
        ;; Going "up" from root just gives you root (it's its own parent).
        :root path-pieces
        (pop path-pieces)))

;; Core Functions

(defn absolute-path?
  "Returns true if the given argument is an absolute path."
  [path]
  (.isAbsolute (File. path)))

(defn normalize
  "Cleans up a path so that it has no leading/trailing whitespace, and
   removes any unremovable same-/parent-dir references."
  [path]
  (let [path-pieces (parse-path path)]
    (loop [result [(first path-pieces)]
           remaining-path (rest path-pieces)]
      (let [[curr & remainder] remaining-path]
        (condp = curr
                nil (render-path result)
                ;; Ignore a repeated separator (empty path component) or
                ;; a same-dir component.
                "" (recur result remainder)
                "." (recur result remainder)
                ".." (recur (up-dir result) remainder)
                (recur (conj result curr) remainder))))))

(defn relativize
  "Takes two absolute paths or two relative paths, and returns a relative path
   that indicates the same file system location as destination-path, but
   relative to base-path."
  [base-path dest-path]
  (let [base-path (parse-path (normalize base-path))
        dest-path (parse-path (normalize dest-path))
        common-path (common-prefix base-path dest-path)
        base-suffix (drop (count common-path) base-path)
        dest-suffix (drop (count common-path) dest-path)]
    (if (nil? common-path)
      (throw (IllegalArgumentException. "Paths contain no common components.")))
    (render-path (concat [:cwd]
                         (repeat (count base-suffix) "..")
                         (loop [suffix []
                                remainder dest-suffix]
                           (let [curr (first remainder)]
                             (condp = curr
                                 nil suffix
                                 "" (recur suffix (rest remainder))
                                 "." (recur suffix (rest remainder))
                                 ".." (recur (conj suffix "..")
                                             (rest remainder))
                                 (recur (conj suffix curr)
                                        (rest remainder)))))))))

(defn resolve
  "Resolve the other-path against the base-path. If other-path is absolute,
   the result is other-path. If other-path is nil, the result is base-path.
   Otherwise, the result is other-path concatenated onto base-path. Does not
   normalize its output."
  [base-path other-path]
  (let [base-path (str base-path)
        other-path (str other-path)]
    (cond (nil? other-path)
          base-path
          (absolute-path? other-path)
          other-path
          :else
          (let [base-components (parse-path (normalize base-path))
                ;; Skip the first element to get rid of the :cwd.
                other-components (rest (parse-path (normalize other-path)))]
            (render-path (concat base-components other-components))))))