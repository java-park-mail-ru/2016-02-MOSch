package base;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import rest.UserProfile;
import supportclasses.LoginScoreSet;

import java.util.List;

/**
 * MOSch-team test server for "Kill The Birds" game
 */
@SuppressWarnings("unused")
public interface AccountService {
    @Nullable
    List<LoginScoreSet> getTopUsers();

    long countUsers();

    @Nullable
    UserProfile getUserByID(long userID);

    @Nullable
    UserProfile getUserByLogin(@NotNull String username);

    @Nullable
    UserProfile getUserBySessionID(@NotNull String sessionID);

    @Nullable
    Long addUser(@NotNull UserProfile userProfile);

    boolean updateUser(long userID, @NotNull UserProfile user, @Nullable Integer multiplier);

    boolean updateUser(@NotNull String sessionID, @NotNull UserProfile user);

    void removeUser(long userID);

    void removeUser(@NotNull String sessionID);

    void removeAll();

    @Nullable
    Long getUserID(@NotNull String userName, @NotNull String password);

    boolean loginUser(@NotNull String userName, @NotNull String password, @NotNull String sessionID);

    void logoutUser(@NotNull String sessionID);
}
