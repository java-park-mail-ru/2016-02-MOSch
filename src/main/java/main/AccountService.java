package main;

import rest.UserProfile;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * MOSch-team test server for "Kill The Birds" game
 */
public class AccountService {
    private Map<String, UserProfile> users = new HashMap<>();
    private Map<String, UserProfile> activeUsers = new HashMap<>();

    public AccountService() {
        users.put("Tolya", new UserProfile("Tolya", "12345", 0L));
        users.put("Kolya", new UserProfile("Kolya", "12345", 1L));
        users.put("Lesha", new UserProfile("Lesha", "12345", 2L, UserProfile.roleEnum.admin));

    }

    public Collection<UserProfile> getAllUsers() {
        return users.values();
    }

    public Collection<UserProfile> getAllActiveUsers() {
        return activeUsers.values();
    }
    public int countUsers() {return users.size();}
    public int countActiveUsers() {return activeUsers.size();}


    public boolean addUser(UserProfile userProfile) {
        Long id = (long)countUsers()+1;
        if (users.containsKey(userProfile.getLogin()))
            return false;
        userProfile.setId(id);
        userProfile.setRole(UserProfile.roleEnum.user);
        users.put(userProfile.getLogin(), userProfile);
        return true;
    }

    public boolean addActiveUser(UserProfile userProfile, String sessionId) {
        UserProfile user = getUser(userProfile.getLogin()); //due to id-less

        if (activeUsers.containsKey(sessionId)) {
            return false;
        }
        activeUsers.put(sessionId, user);
        return true;
    }

    public void removeActiveUser(String sessionId){
        activeUsers.remove(sessionId);
    }

    public void removeActiveUser(Long id){
        for (UserProfile user : this.getAllActiveUsers() ) {
            if (user.getId() == id)
                getAllActiveUsers().remove(user);
        }
    }

    public UserProfile getActiveUser(String sessionId){return activeUsers.get(sessionId);}
    public UserProfile getUser(String userName) {return users.get(userName);}
    public UserProfile getUser(Long id) {
        for (UserProfile user : this.getAllUsers() )
        {
            if (user.getId() == id)
                return user;
        }
        return null;

    }




}