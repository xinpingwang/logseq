(ns frontend.modules.outliner.utils
  (:require [frontend.db.conn :as conn]
            [frontend.db.outliner :as db-outliner]
            [datascript.impl.entity :as e]
            [frontend.util :as util]))

(defn block-id?
  [id]
  (or
    (number? id)
    (string? id)))

(defn check-block-id
  [id]
  (assert (block-id? id)
    (util/format "The id should match block-id?: %s" (pr-str id))))

(defn ->block-id
  [id]
  (cond
    (block-id? id)
    id

    (and
      (vector? id)
      (= (first id) :block/id))
    (second id)

    (or (e/entity? id) (map? id))
    (let [conn (conn/get-conn false)]
      (-> (db-outliner/get-by-id conn (:db/id id))
        (:block/id)))

    :else nil))

(defn ->block-lookup-ref
  "
  string? or number?  -> [:block/id x]
  [:block/id x] -> [:block/id x]
  {:db/id x} -> {:db/id x}
  :else -> nil
  "
  [id]
  (cond
    (and
      (vector? id)
      (= (first id) :block/id))
    id

    (block-id? id)
    [:block/id id]

    (or (e/entity? id) (map? id))
    id

    :else nil))
