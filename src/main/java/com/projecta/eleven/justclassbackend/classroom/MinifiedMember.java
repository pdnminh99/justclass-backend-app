package com.projecta.eleven.justclassbackend.classroom;

import com.google.cloud.Timestamp;
import com.google.cloud.firestore.DocumentSnapshot;
import com.projecta.eleven.justclassbackend.user.MinifiedUser;
import com.projecta.eleven.justclassbackend.utils.MapSerializable;

import java.util.HashMap;

class MinifiedMember extends MinifiedUser implements MapSerializable {

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

    public static MinifiedMember toMinifiedMember(MinifiedUser user, MemberRoles role, Timestamp join) {
        MinifiedMember member = (MinifiedMember) user;
        member.setRole(role);
        member.setJoinDatetime(join);
        return member;
    }

    @Override
    public HashMap<String, Object> toMap(boolean isTimestampInMilliseconds) {
        var map = super.toMap();
        if (role != null) {
            ifFieldNotNullThenPutToMap("role", role.toString(), map);
        }
        if (joinDatetime != null) {
            ifFieldNotNullThenPutToMap("joinDatetime", isTimestampInMilliseconds ?
                    joinDatetime.toDate().getTime() :
                    joinDatetime, map);
        }
        return map;
    }
}
