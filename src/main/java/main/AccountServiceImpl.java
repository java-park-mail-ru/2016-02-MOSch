package main;

import db.datasets.UserDataSet;
import db.datasets.UserDataSetDAO;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.jetbrains.annotations.NotNull;
import rest.UserProfile;
import supportClasses.LoginScoreSet;
import supportClasses.MD5Hash;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;


/**
 * MOSch-team test server for "Kill The Birds" game
 */

public class AccountServiceImpl implements AccountService {
    final SessionFactory sessionFactory;
    private final Map<String, UserProfile> loggedUsers = new ConcurrentHashMap<>();
    private final Map<Long, String> sessionIDs = new ConcurrentHashMap<>();

    public AccountServiceImpl(@NotNull String config) {
        final Configuration configuration = new Configuration();
        configuration.addAnnotatedClass(UserDataSet.class);
        configuration.configure(config);
        sessionFactory = configuration.buildSessionFactory();
    }


    @Override
    public List<UserProfile> getAllUsers() {
        final Session session = sessionFactory.openSession();
        final UserDataSetDAO dao = new UserDataSetDAO(session);
        final List<UserDataSet> userDS = dao.readAll();
        session.close();
        return userDS.stream().map(UserProfile::new).collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    public List<LoginScoreSet> getTopUsers() {
        final Session session = sessionFactory.openSession();
        final UserDataSetDAO dao = new UserDataSetDAO(session);
        final List<LoginScoreSet> ds = dao.readTop();
        session.close();
        return ds;

    }

    @Override
    public long countUsers() {
        //noinspection HibernateResourceOpenedButNotSafelyClosed
        final Session session = sessionFactory.openSession();
        final Long result = new UserDataSetDAO(session).countUsers();
        session.close();
        return result;
    }

    @Override
    public UserProfile getUserByID(long userID) {
        final Session session = sessionFactory.openSession();
        final UserDataSetDAO dao = new UserDataSetDAO(session);
        final UserDataSet dataSet = dao.readUserByID(userID);
        session.close();
        if (dataSet != null) {
            return new UserProfile(dataSet);
        } else {
            return null;
        }
    }

    @Override
    public UserProfile getUserByLogin(@NotNull String username) {
        final Session session = sessionFactory.openSession();
        final UserDataSetDAO dao = new UserDataSetDAO(session);
        final UserDataSet dataSet = dao.readUserByLogin(username);
        session.close();
        if (dataSet != null) {
            return new UserProfile(dataSet);
        } else {
            return null;
        }
    }

    @Override
    public UserProfile getUserBySessionID(@NotNull String sessionID) {
        if (loggedUsers.containsKey(sessionID)) {
            return loggedUsers.get(sessionID);
        } else {
            return null;
        }
    }

    @Override
    public Long addUser(@NotNull UserProfile userProfile) {
        final Session session = sessionFactory.openSession();
        if (getUserByLogin(userProfile.getLogin()) != null) {
            return null;
        }
        final Long result;
        final UserDataSetDAO dao = new UserDataSetDAO(session);
        final UserDataSet userDS = new UserDataSet(userProfile);

        if (dao.createUser(userDS)) {
            result = userDS.getId();
        } else {
            result = null;
            System.out.println("Пользователь НЕ добавлен");
        }
        session.close();
        return result;
    }

    @Override
    public void updateUser(long userID, @NotNull UserProfile user) {
        final Session session = sessionFactory.openSession();
        final UserDataSetDAO dao = new UserDataSetDAO(session);
        dao.updateUser(userID, new UserDataSet(user));
        session.close();
    }

    @Override
    public void updateUser(@NotNull String sessionID, @NotNull UserProfile user) {
        if (isLoggedIn(sessionID)) {
            final UserProfile profile = getUserBySessionID(sessionID);
            if (profile != null) {
                updateUser(profile.getId(), user);
                loggedUsers.put(sessionID, user);
            }
        }
    }

    @Override
    public void removeUser(long userID) {
        final Session session = sessionFactory.openSession();
        final UserDataSetDAO dao = new UserDataSetDAO(session);
        dao.deleteUser(userID);
        session.close();
    }

    @Override
    public void removeUser(@NotNull String sessionID) {
        if (isLoggedIn(sessionID)) {
            final UserProfile profile = getUserBySessionID(sessionID);
            if (profile != null) {
                removeUser(profile.getId());
            }
            logoutUser(sessionID);
        }
    }

    @Override
    public boolean isLoggedIn(@NotNull String sessionID) {
        return loggedUsers.containsKey(sessionID);
    }

    @Override
    public Long getUserID(@NotNull String userName, @NotNull String password) {
        final UserProfile profile = getUserByLogin(userName);
        if (profile != null) {
            return (Objects.equals(profile.getPassword(), password)) ? profile.getId() : null;
        }
        return null;
    }

    @Override
    public String loginUser(@NotNull String userName, @NotNull String password) {
        final Long userID = getUserID(userName, password);
        if (userID != null) {
            if (sessionIDs.containsKey(userID)) {
                return sessionIDs.get(userID);
            } else {
                final String sessionID = MD5Hash.getHashString(userName);
                sessionIDs.put(userID, sessionID);
                loggedUsers.put(sessionID, getUserByID(userID));
                return sessionID;
            }
        } else {
            return null;
        }
    }

    @Override
    public String loginUser(@NotNull String sessionID) {
        if (loggedUsers.containsKey(sessionID)) {
            return sessionID;
        } else {
            return null;
        }
    }

    @Override
    public void logoutUser(@NotNull String sessionID) {
        if (loggedUsers.containsKey(sessionID)) {
            sessionIDs.remove(loggedUsers.get(sessionID).getId());
            loggedUsers.remove(sessionID);
        }
    }

}
