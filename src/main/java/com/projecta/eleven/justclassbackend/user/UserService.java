package com.projecta.eleven.justclassbackend.user;

import com.google.api.core.ApiFutures;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class UserService implements IUserOperations {

    private final IUserRepository repository;

    @Autowired
    public UserService(IUserRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<User> assignUser(UserRequestBody requestUser, Boolean autoUpdate)
            throws ExecutionException, InterruptedException, InvalidUserInformationException {
        if (Objects.isNull(requestUser)) {
            return Optional.empty();
        }
        if (!verifyValidStringField(requestUser.getLocalId())) {
            throw new InvalidUserInformationException("LocalId must not be null or empty.");
        }
        var existingUser = repository.getUser(requestUser);
        if (existingUser.isPresent()) {
            return Objects.nonNull(autoUpdate) && !autoUpdate ?
                    existingUser :
                    compareAndApplyChanges(existingUser.get(), requestUser);
        }
        if (verifyValidStringField(requestUser.getEmail()) && verifyValidStringField(requestUser.getDisplayName())) {
            return repository.createUser(requestUser);
        }
        throw new InvalidUserInformationException(
                "Email and displayName must not be null or empty.",
                new NullPointerException("Email or displayName field is null."));
    }

    @Override
    public DocumentReference getUserReference(String localId) {
        return repository.getUserReference(localId);
    }

    @Override
    public Stream<DocumentReference> getUsersReferences(List<String> localIds) {
        return localIds.stream().map(repository::getUserReference);
    }

    @Override
    public Stream<QueryDocumentSnapshot> getUsersByEmail(List<String> emails) throws ExecutionException, InterruptedException {
        return repository.getUsersByEmail(emails);
    }

    private Optional<User> compareAndApplyChanges(User existingUser, UserRequestBody newUser) {
        // ALERT: changesMap and existingUser is mutable throughout this method.
        var changesMap = new HashMap<String, Object>();

        applyChangesIfValid(
                "firstName",
                existingUser.getFirstName(),
                newUser.getFirstName(),
                existingUser,
                changesMap
        );
        applyChangesIfValid(
                "lastName",
                existingUser.getLastName(),
                newUser.getLastName(),
                existingUser,
                changesMap
        );
        applyChangesIfValid(
                "displayName",
                existingUser.getDisplayName(),
                newUser.getDisplayName(),
                existingUser,
                changesMap
        );
        applyChangesIfValid(
                "email",
                existingUser.getEmail(),
                newUser.getEmail(),
                existingUser,
                changesMap
        );
        applyChangesIfValid(
                "photoUrl",
                existingUser.getPhotoUrl(),
                newUser.getPhotoUrl(),
                existingUser,
                changesMap
        );
        if (changesMap.isEmpty()) {
            return Optional.of(existingUser);
        }
        repository.edit(existingUser.getLocalId(), changesMap);
        return Optional.of(existingUser);
    }

    private void applyChangesIfValid(String key, String oldStringValue, String newStringValue, User existingUser, HashMap<String, Object> changesMap) {
        if (allowChangesApplied(oldStringValue, newStringValue)) {
            changesMap.put(key, newStringValue);
            switch (key) {
                case "firstName":
                    existingUser.setFirstName(newStringValue);
                    break;
                case "lastName":
                    existingUser.setLastName(newStringValue);
                    break;
                case "displayName":
                    existingUser.setDisplayName(newStringValue);
                    break;
                case "email":
                    existingUser.setEmail(newStringValue);
                    break;
                case "photoUrl":
                    existingUser.setPhotoUrl(newStringValue);
                    break;
                default:
                    break;
            }
        }
    }

    private boolean allowChangesApplied(String oldStringValue, String newStringValue) {
        return (Objects.isNull(oldStringValue) || !oldStringValue.equals(newStringValue)) && verifyValidStringField(newStringValue);
    }

    private boolean verifyValidStringField(String value) {
        return Objects.nonNull(value) && value.trim().length() != 0;
    }

    @Override
    public Stream<User> getFriendsOfUser(String localId, Timestamp lastTimeRequest)
            throws ExecutionException, InterruptedException {
        if (!verifyValidStringField(localId)) {
            return Stream.empty();
        }
        var relationshipsReferences = repository
                .getRelationshipReferences(localId, lastTimeRequest)
                .map(ref -> ref.getGuestId().equals(localId) ?
                        ref.getHostReference().get() :
                        ref.getGuestReference().get())
                .collect(Collectors.toList());
        return ApiFutures.allAsList(relationshipsReferences)
                .get()
                .stream()
                .map(u -> new User(u, false));
    }

    @Override
    public void createFriendIfNotExist(FriendReference friend) throws ExecutionException, InterruptedException {
        List<User> friends = getFriendsOfUser(friend.getGuestId(), null)
                .collect(Collectors.toList());

        for (var person : friends) {
            if (person.getLocalId().equals(friend.getHostId())) {
                return;
            }
        }

        repository.createFriend(friend);
    }
}
