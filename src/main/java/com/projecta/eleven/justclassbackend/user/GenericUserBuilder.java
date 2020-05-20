package com.projecta.eleven.justclassbackend.user;

import com.google.cloud.firestore.DocumentSnapshot;

public abstract class GenericUserBuilder<B extends GenericUserBuilder<B>> {
    protected String localId;

    protected String displayName;

    protected String photoUrl;

    protected String email;

    public B setLocalId(String localId) {
        this.localId = localId;
        return self();
    }

    public B setDisplayName(String displayName) {
        this.displayName = displayName;
        return self();
    }

    public B setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
        return self();
    }

    public B setEmail(String email) {
        this.email = email;
        return self();
    }

    public B fromSnapshot(DocumentSnapshot snapshot) {
        this.localId = snapshot.getId();
        this.displayName = snapshot.getString("displayName");
        this.photoUrl = snapshot.getString("photoUrl");
        this.email = snapshot.getString("email");
        return self();
    }

    protected abstract B self();

}
