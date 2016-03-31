package main;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import rest.UserProfile;

import java.util.List;

/**
 * MOSch-team test server for "Kill The Birds" game
 */
@SuppressWarnings("unused")
public interface AccountService {

    List<UserProfile> getAllUsers();

    long countUsers();

    @Nullable
    UserProfile getUserByID(long userID);

    @Nullable
    UserProfile getUserByLogin(@NotNull String username);

    @Nullable
    UserProfile getUserBySessionID(@NotNull String sessionID); // sessionID is the same that the 'auth_token'

    @Nullable
    Long addUser(@NotNull UserProfile userProfile);

    void updateUser(long userID, @NotNull UserProfile user);

    void updateUser(@NotNull String sessionID, @NotNull UserProfile user);

    void removeUser(long userID);

    void removeUser(@NotNull String sessionID);

    boolean isLoggedIn(@NotNull String sessionID);

    @Nullable
    Long isUserExists(@NotNull String userName, @NotNull String password);

    @Nullable
    String loginUser(@NotNull String userName, @NotNull String password);

    @Nullable
    String loginUser(@NotNull String sessionID);

    void logoutUser(@NotNull String sessionID);

}
