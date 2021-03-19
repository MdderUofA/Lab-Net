package com.example.lab_net;

import com.google.firebase.firestore.QueryDocumentSnapshot;

public class SearchableUser extends Searchable {
    @Override
    public SearchableUser applyFromDatabase(QueryDocumentSnapshot snapshot) {
        this.name = (String)snapshot.get("firstName") + " " + (String)snapshot.get("lastName");
        this.description = (String)snapshot.get("email");
        this.date = null;
        this.reference = new SearchableDocumentReference("UserProfile",
                snapshot.getId());
        return this;
    }
}
