package com.projecta.eleven.justclassbackend.classroom;

import com.google.cloud.Timestamp;
import com.projecta.eleven.justclassbackend.user.GenericUserBuilder;

abstract class GenericMinifiedMemberBuilder<B extends GenericMinifiedMemberBuilder<B>> extends GenericUserBuilder<B> {
    protected MemberRoles role;

    protected Timestamp joinDatetime;

    public B setRole(MemberRoles role) {
        this.role = role;
        return self();
    }

    public B setJoinDatetime(Timestamp joinDatetime) {
        this.joinDatetime = joinDatetime;
        return self();
    }
}

public final class MinifiedMemberBuilder extends GenericMinifiedMemberBuilder<MinifiedMemberBuilder> {

    public MinifiedMember build() {
        return new MinifiedMember(localId, displayName, photoUrl, email, role, joinDatetime);
    }

    @Override
    protected MinifiedMemberBuilder self() {
        return this;
    }
}