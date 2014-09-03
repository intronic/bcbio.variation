(ns bcbio.variation.core
  (:import [org.broadinstitute.sting.gatk CommandLineGATK])
  (:require [clojure.string :as string]
            [bcbio.variation.compare]
            [bcbio.variation.combine]
            [bcbio.variation.haploid]
            [bcbio.align.reorder]
            [bcbio.variation.utils.core])
#_  (:gen-class))

(def ^{:doc "Mapping of special command line arguments to main functions"
       :private true}
  altmain-map
  {:compare bcbio.variation.compare/-main
   :prep bcbio.variation.combine/-main
   :haploid bcbio.variation.haploid/-main
   :reorder bcbio.align.reorder/-main
   :utils bcbio.variation.utils.core/-main})

(defn- get-altmain-fn
  "Retrieve alternative main functions based on first argument."
  [arg]
  (when (and (not (nil? arg))
             (.startsWith arg "variant-"))
    (get altmain-map
         (keyword (string/replace-first arg "variant-" "")))))

(defn -main [& args]
  (if-let [alt-fn (get-altmain-fn (first args))]
    (do
      (apply alt-fn (rest args))
      (System/exit 0))
    (CommandLineGATK/main (into-array (if-not (nil? args) args ["-h"])))))
