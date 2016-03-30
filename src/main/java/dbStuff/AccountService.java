package dbStuff;

import dbStuff.dataSets.*;
import rest.UserProfile;

import java.util.Collection;

/**
 * MOSch-team test server for "Kill The Birds" game
 */

@SuppressWarnings("unused")
public interface AccountService {

    Collection getAllUsers();

    Collection<UserProfile> getAllActiveUsers();

    Long countUsers();

    Long countActiveUsers();

    boolean addUser(UserProfile userProfile);

    void addActiveUser(UserDataSet userProfile, String authToken);

    void removeActiveUser(String authToken );

    void removeActiveUser(Long id);

    AuthDataSet getActiveUser(String sessionId);

    UserDataSet getUser(Long id);

    UserDataSet getUserDS(String login);

    void updateUser(UserDataSet user);

    void removeUser(UserDataSet user);
}
