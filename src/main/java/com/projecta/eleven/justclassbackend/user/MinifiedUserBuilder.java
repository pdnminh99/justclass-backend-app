package com.projecta.eleven.justclassbackend.user;

public final class MinifiedUserBuilder extends GenericUserBuilder<MinifiedUserBuilder> {

    public static MinifiedUserBuilder newBuilder() {
        return new MinifiedUserBuilder();
    }

    public MinifiedUser build() {
        return new MinifiedUser(localId, displayName, photoUrl, email);
    }

    @Override
    protected MinifiedUserBuilder self() {
        return this;
    }
}
