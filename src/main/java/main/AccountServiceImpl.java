package main;

import db.datasets.UserDataSet;
import db.datasets.UserDataSetDAO;
import org.hibernate.*;
import org.hibernate.cfg.Configuration;
import org.hibernate.Transaction;
import org.hibernate.resource.transaction.spi.*;
import org.jetbrains.annotations.NotNull;
import rest.UserProfile;
import supportclasses.*;

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
    public List<UserProfile> getAllUsers(){
        try(Session session = sessionFactory.openSession()) {
            try {
                final Transaction transaction = session.beginTransaction();
                final UserDataSetDAO dao = new UserDataSetDAO(session);
                final List<UserDataSet> userDS = dao.readAll();
                transaction.commit();
                session.close();
                return userDS.stream().map(UserProfile::new).collect(Collectors.toCollection(LinkedList::new));
            } catch (HibernateException e) {
                if (session.getTransaction().getStatus() == TransactionStatus.ACTIVE
                        || session.getTransaction().getStatus() == TransactionStatus.MARKED_ROLLBACK) {
                    session.getTransaction().rollback();
                }
                return null;
            }
        }
    }

    @Override
    public List<LoginScoreSet> getTopUsers() {
        try(Session session = sessionFactory.openSession()) {
            try {
                final Transaction transaction = session.beginTransaction();
                final UserDataSetDAO dao = new UserDataSetDAO(session);
                final List<LoginScoreSet> ds = dao.readTop();
                transaction.commit();
                session.close();
                return ds;
            } catch (HibernateException e) {
                if (session.getTransaction().getStatus() == TransactionStatus.ACTIVE
                        || session.getTransaction().getStatus() == TransactionStatus.MARKED_ROLLBACK) {
                    session.getTransaction().rollback();
                }
                return null;
            }
        }
    }

    @Override
    public long countUsers() {
        try(Session session = sessionFactory.openSession()) {
            try {
                final Transaction transaction = session.beginTransaction();
                final Long result = new UserDataSetDAO(session).countUsers();
                transaction.commit();
                return result;
            }
            catch (HibernateException e) {
                if (session.getTransaction().getStatus() == TransactionStatus.ACTIVE
                        || session.getTransaction().getStatus() == TransactionStatus.MARKED_ROLLBACK) {
                    session.getTransaction().rollback();
                }
                return -1L;
            }
        }
    }

    @Override
    public UserProfile getUserByID(long userID) {
        try (Session session = sessionFactory.openSession()) {
            try {
                final Transaction transaction = session.beginTransaction();
                final UserDataSetDAO dao = new UserDataSetDAO(session);
                final UserDataSet dataSet = dao.readUserByID(userID);

                transaction.commit();
                if (dataSet != null) {
                    return new UserProfile(dataSet);
                } else return null;
            }
            catch (HibernateException e) {
                if (session.getTransaction().getStatus() == TransactionStatus.ACTIVE
                        || session.getTransaction().getStatus() == TransactionStatus.MARKED_ROLLBACK) {
                    session.getTransaction().rollback();
                }
                return null;
            }
        }
    }

    @Override
    public UserProfile getUserByLogin(@NotNull String username) {
        try(Session session = sessionFactory.openSession()) {
            try {
                final Transaction transaction = session.beginTransaction();
                final UserDataSetDAO dao = new UserDataSetDAO(session);
                final UserDataSet dataSet = dao.readUserByLogin(username);
                transaction.commit();
                if (dataSet != null) {
                    return new UserProfile(dataSet);
                } else return null;
            }
            catch (HibernateException e) {
                if (session.getTransaction().getStatus() == TransactionStatus.ACTIVE
                        || session.getTransaction().getStatus() == TransactionStatus.MARKED_ROLLBACK) {
                    session.getTransaction().rollback();
                }
                return null;
            }
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
        if (!userProfile.checkLogin()) {
            return null;
        }
        if (!userProfile.checkPassword()) {
            return null;
        }
        try(Session session = sessionFactory.openSession()) {
            try {
                final Transaction transaction = session.beginTransaction();
                if (getUserByLogin(userProfile.getLogin()) != null) {
                    return null;
                }
                final UserDataSetDAO dao = new UserDataSetDAO(session);
                final UserDataSet userDS = new UserDataSet(userProfile);
                final Long result;

                if (dao.createUser(userDS)) {
                    result = userDS.getId();
                } else {
                    result = null;
                }
                transaction.commit();
                return result;
            }
            catch (HibernateException e) {
                if (session.getTransaction().getStatus() == TransactionStatus.ACTIVE
                        || session.getTransaction().getStatus() == TransactionStatus.MARKED_ROLLBACK) {
                    session.getTransaction().rollback();
                }
                return null;
            }
        }
    }

    @Override
    public void updateUser(long userID, @NotNull UserProfile user) {
        try(Session session = sessionFactory.openSession()) {
            try {
                final Transaction transaction = session.beginTransaction();
                final UserDataSetDAO dao = new UserDataSetDAO(session);
                dao.updateUser(userID, new UserDataSet(user));
                transaction.commit();
            }
            catch (HibernateException e) {
                if (session.getTransaction().getStatus() == TransactionStatus.ACTIVE
                        || session.getTransaction().getStatus() == TransactionStatus.MARKED_ROLLBACK) {
                    session.getTransaction().rollback();
                }
            }
        }
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
        try (Session session = sessionFactory.openSession()) {
            try {
                final Transaction transaction = session.beginTransaction();
                final UserDataSetDAO dao = new UserDataSetDAO(session);
                dao.deleteUser(userID);
                transaction.commit();
            }
            catch (HibernateException e) {
                if (session.getTransaction().getStatus() == TransactionStatus.ACTIVE
                        || session.getTransaction().getStatus() == TransactionStatus.MARKED_ROLLBACK) {
                    session.getTransaction().rollback();
                }
            }
        }
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
