package com.projecta.eleven.justclassbackend.classroom;

import com.google.cloud.Timestamp;
import com.google.cloud.firestore.DocumentSnapshot;
import com.projecta.eleven.justclassbackend.user.MinifiedUser;

class MinifiedMember extends MinifiedUser {

    private MemberRoles role;

    private Timestamp joinDatetime;

    public MinifiedMember(String localId, String displayName, String photoUrl, MemberRoles role, Timestamp joinDatetime) {
        super(localId, displayName, photoUrl);
        this.role = role;
        this.joinDatetime = joinDatetime;
    }

    public MinifiedMember(DocumentSnapshot snapshot) {
        super(snapshot);
    }

    public MemberRoles getRole() {
        return role;
    }

    public void setRole(MemberRoles role) {
        this.role = role;
    }

    public Timestamp getJoinDatetime() {
        return joinDatetime;
    }

    public void setJoinDatetime(Timestamp joinDatetime) {
        this.joinDatetime = joinDatetime;
    }
}
