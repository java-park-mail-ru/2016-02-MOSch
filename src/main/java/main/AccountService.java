package main;

import rest.UserProfile;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.Objects;
import java.security.*;

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
