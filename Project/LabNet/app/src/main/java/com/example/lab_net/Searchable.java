package com.example.lab_net;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Date;

/**
 * An interface that represents an object that can be searched for. Note that anything that
 * implements this must contian a null constructor as applyFromDatabase is invoked reflectively.
 * @see SearchableList
 */
public abstract class Searchable {

    protected String name = "NOT_YET_DEFINED";
    protected String description = "NOT_YET_DEFINED";
    protected Date date = null;
    protected SearchableDocumentReference reference = null;

    public Searchable(){}; // empty, used for reflection.

    /**
     * Creates a new Searchable given the data.
     * @param name The name associated with this Searchable.
     * @param description The description associated with this Searchable.
     * @param reference The document reference to to query for this searchable, or null
     *                  if not applicable.
     */
    public Searchable(String name, String description,
                                       SearchableDocumentReference reference) {
        this.name = name;
        this.description = description;
        this.reference = reference;
        this.date = null;

    }

    /**
     * Applies the specified data to this searchable from a document in the Database.
     * @param snapshot The document snapshot to get the data from.
     * @return Itself.
     */
    public abstract Searchable applyFromDatabase(QueryDocumentSnapshot snapshot);

    /**
     * Getter for the name associated with this Searchable
     * @return The name associated with this Searchable.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Getter for the description associated with this Searchable
     * @return The description associated with this Searchable.
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Getter for the date associated with this Searchable
     * @return The date associated with this Searchable.
     */
    public Date getDate() {
        return this.date;
    }

    /**
     * Getter for the SearchableDocumentReference associated with this Searchable
     * @return The SearchableDocumentReference associated with this Searchable, or null
     *          if not applicable.
     */
    public SearchableDocumentReference getDocumentReference() {
        return this.reference;
    }

}
