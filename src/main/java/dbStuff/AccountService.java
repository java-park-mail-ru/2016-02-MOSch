package dbStuff;

import rest.UserProfile;

import java.util.Collection;

/**
 * MOSch-team test server for "Kill The Birds" game
 */
public interface AccountService {

    Collection<UserProfile> getAllUsers();

    Collection<UserProfile> getAllActiveUsers();

    int countUsers();

    int countActiveUsers();

    boolean addUser(UserProfile userProfile);

    boolean addActiveUser(UserProfile userProfile, String sessionId);

    void removeActiveUser(String sessionId);

    void removeActiveUser(Long id);

    UserProfile getActiveUser(String sessionId);

    UserProfile getUser(String userName);

    UserProfile getUser(Long id);
}
