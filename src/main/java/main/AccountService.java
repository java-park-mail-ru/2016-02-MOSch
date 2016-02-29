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

    public AccountService() {
        users.put("Tolya", new UserProfile("Tolya", "12345", 0L));
        users.put("Kolya", new UserProfile("Kolya", "12345", 1L));
        users.put("Lesha", new UserProfile("Lesha", "12345", 2L));

    }

    public Collection<UserProfile> getAllUsers() {
        return users.values();
    }
    public int countUsers() {return users.size();}



    public boolean addUser(UserProfile userProfile) {
        Long id = (long)countUsers()+1;
        if (users.containsKey(userProfile.getLogin()))
            return false;
        userProfile.setId(id);
        users.put(userProfile.getLogin(), userProfile);
        return true;
    }

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