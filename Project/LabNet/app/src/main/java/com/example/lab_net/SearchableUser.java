package com.example.lab_net;

import com.google.firebase.firestore.QueryDocumentSnapshot;

/**
 * A Searchable which represents a user.
 * @author Marcus
 */
public class SearchableUser extends Searchable {

    @Override
    public SearchableUser applyFromDatabase(QueryDocumentSnapshot snapshot) {
        this.name = (String)snapshot.get("firstName") + " " + (String)snapshot.get("lastName");
        //description == username
        this.description = (String)snapshot.getId();
        this.date = null;
        this.reference = new SearchableDocumentReference("UserProfile",
                snapshot.getId());
        return this;
    }
}
