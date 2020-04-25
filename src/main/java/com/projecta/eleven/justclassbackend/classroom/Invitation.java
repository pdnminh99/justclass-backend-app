package com.projecta.eleven.justclassbackend.classroom;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.lang.Nullable;

import java.util.Objects;

class Invitation {
    @Nullable
    @JsonProperty("localId")
    private String localId;

    @Nullable
    @JsonProperty("email")
    private String email;

    @Nullable
    @JsonProperty("role")
    private MemberRoles role;

    public Invitation(@Nullable String localId,
                      @Nullable String email,
                      @Nullable MemberRoles role) {
        this.localId = localId;
        this.email = email;
        this.role = role;
    }

    @Nullable
    public String getLocalId() {
        return localId;
    }

    public void setLocalId(@Nullable String localId) {
        this.localId = localId;
    }

    @Nullable
    public String getEmail() {
        return email;
    }

    public void setEmail(@Nullable String email) {
        this.email = email;
    }

    @Nullable
    public MemberRoles getRole() {
        return role;
    }

    public void setRole(@Nullable MemberRoles role) {
        this.role = role;
    }

    public boolean equal(Invitation otherInvitation, boolean replaceWithHigherRole) {
        boolean isSameInfo = Objects.equals(email, otherInvitation.email) && Objects.equals(localId, otherInvitation.localId);
        if (isSameInfo && getRole() != otherInvitation.getRole() && replaceWithHigherRole) {
            setRole(MemberRoles.COLLABORATOR);
        }
        return isSameInfo;
    }

    @Override
    public String toString() {
        return "Invitation{" +
                "localId='" + localId + '\'' +
                ", email='" + email + '\'' +
                ", role=" + role +
                '}';
    }
}
