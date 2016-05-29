package main;

import base.AccountService;
import db.datasets.UserDataSet;
import db.datasets.UserDataSetDAO;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.resource.transaction.spi.TransactionStatus;
import org.jetbrains.annotations.NotNull;
import rest.UserProfile;
import supportclasses.LoginScoreSet;

import java.util.List;
import java.util.Objects;

/**
 * MOSch-team test server for "Kill The Birds" game
 */
public class AccountServiceImpl implements AccountService {
    final SessionFactory sessionFactory;

    public AccountServiceImpl(@NotNull String config) {
        final Configuration configuration = new Configuration();
        configuration.addAnnotatedClass(UserDataSet.class);
        configuration.configure(config);
        sessionFactory = configuration.buildSessionFactory();
        try (Session session = sessionFactory.openSession()) {
            try {
                final Transaction transaction = session.beginTransaction();
                UserDataSetDAO.logoutAll(session);
                transaction.commit();
            } catch (HibernateException e) {
                if (session.getTransaction().getStatus() == TransactionStatus.ACTIVE
                        || session.getTransaction().getStatus() == TransactionStatus.MARKED_ROLLBACK) {
                    session.getTransaction().rollback();
                }
            }
        }
    }

    @Override
    public List<LoginScoreSet> getTopUsers() {
        try (Session session = sessionFactory.openSession()) {
            try {
                final Transaction transaction = session.beginTransaction();
                final UserDataSetDAO dao = new UserDataSetDAO(session);
                final List<LoginScoreSet> ds = dao.readAll();
                transaction.commit();
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
        try (Session session = sessionFactory.openSession()) {
            try {
                final Transaction transaction = session.beginTransaction();
                final Long result = new UserDataSetDAO(session).countUsers();
                transaction.commit();
                return result;
            } catch (HibernateException e) {
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
    public UserProfile getUserByLogin(@NotNull String username) {
        try (Session session = sessionFactory.openSession()) {
            try {
                final Transaction transaction = session.beginTransaction();
                final UserDataSetDAO dao = new UserDataSetDAO(session);
                final UserDataSet dataSet = dao.readUserByLogin(username);
                transaction.commit();
                if (dataSet != null) {
                    return new UserProfile(dataSet);
                } else return null;
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
    public UserProfile getUserBySessionID(@NotNull String sessionID) {
        try (Session session = sessionFactory.openSession()) {
            try {
                final Transaction transaction = session.beginTransaction();
                final UserDataSetDAO dao = new UserDataSetDAO(session);
                final UserDataSet dataSet = dao.readUserBySessionID(sessionID);
                transaction.commit();
                if (dataSet != null) {
                    return new UserProfile(dataSet);
                } else return null;
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
    public Long addUser(@NotNull UserProfile userProfile) {
        if (!userProfile.checkUsername()) {
            return null;
        }
        if (!userProfile.checkPassword()) {
            return null;
        }
        try (Session session = sessionFactory.openSession()) {
            try {
                final Transaction transaction = session.beginTransaction();
                if (getUserByLogin(userProfile.getUsername()) != null) {
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
    public boolean updateUser(long userID, @NotNull UserProfile user) {
        try (Session session = sessionFactory.openSession()) {
            try {
                final Transaction transaction = session.beginTransaction();
                final UserDataSetDAO dao = new UserDataSetDAO(session);
                dao.updateUser(userID, new UserDataSet(user), null);
                transaction.commit();
                return true;
            } catch (HibernateException e) {
                if (session.getTransaction().getStatus() == TransactionStatus.ACTIVE
                        || session.getTransaction().getStatus() == TransactionStatus.MARKED_ROLLBACK) {
                    session.getTransaction().rollback();
                }
                return false;
            }
        }
    }

    @Override
    public boolean updateUser(@NotNull String sessionID, @NotNull UserProfile user) {
        try (Session session = sessionFactory.openSession()) {
            try {
                final Transaction transaction = session.beginTransaction();
                final UserDataSetDAO dao = new UserDataSetDAO(session);
                final UserDataSet userDataSet = dao.readUserBySessionID(sessionID);
                if (userDataSet != null) {
                    final long userID = userDataSet.getId();
                    dao.updateUser(userID, new UserDataSet(user), null);
                }
                transaction.commit();
                return true;
            } catch (HibernateException e) {
                if (session.getTransaction().getStatus() == TransactionStatus.ACTIVE
                        || session.getTransaction().getStatus() == TransactionStatus.MARKED_ROLLBACK) {
                    session.getTransaction().rollback();
                }
                return false;
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
            } catch (HibernateException e) {
                if (session.getTransaction().getStatus() == TransactionStatus.ACTIVE
                        || session.getTransaction().getStatus() == TransactionStatus.MARKED_ROLLBACK) {
                    session.getTransaction().rollback();
                }
            }
        }
    }

    @Override
    public void removeUser(@NotNull String sessionID) {
        try (Session session = sessionFactory.openSession()) {
            try {
                final Transaction transaction = session.beginTransaction();
                final UserDataSetDAO dao = new UserDataSetDAO(session);
                final UserDataSet userDataSet = dao.readUserBySessionID(sessionID);
                if (userDataSet != null) {
                    final long userID = userDataSet.getId();
                    dao.deleteUser(userID);
                }
                transaction.commit();
            } catch (HibernateException e) {
                if (session.getTransaction().getStatus() == TransactionStatus.ACTIVE
                        || session.getTransaction().getStatus() == TransactionStatus.MARKED_ROLLBACK) {
                    session.getTransaction().rollback();
                }
            }
        }
    }

    @Override
    public void removeAll() {
        try (Session session = sessionFactory.openSession()) {
            try {
                final Transaction transaction = session.beginTransaction();
                UserDataSetDAO.deleteAll(session);
                transaction.commit();
            } catch (HibernateException e) {
                if (session.getTransaction().getStatus() == TransactionStatus.ACTIVE
                        || session.getTransaction().getStatus() == TransactionStatus.MARKED_ROLLBACK) {
                    session.getTransaction().rollback();
                }
            }
        }
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
    public boolean loginUser(@NotNull String userName, @NotNull String password, @NotNull String sessionID) {
        final Long userID = getUserID(userName, password);
        if (userID != null) {
            try (Session session = sessionFactory.openSession()) {
                try {
                    final Transaction transaction = session.beginTransaction();
                    final UserDataSetDAO dao = new UserDataSetDAO(session);
                    final String result = dao.loginUser(userID, sessionID);
                    transaction.commit();
                    return result != null;
                } catch (HibernateException e) {
                    if (session.getTransaction().getStatus() == TransactionStatus.ACTIVE
                            || session.getTransaction().getStatus() == TransactionStatus.MARKED_ROLLBACK) {
                        session.getTransaction().rollback();
                    }
                }
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public void logoutUser(@NotNull String sessionID) {
        try (Session session = sessionFactory.openSession()) {
            try {
                final Transaction transaction = session.beginTransaction();
                final UserDataSetDAO dao = new UserDataSetDAO(session);
                final UserDataSet userDataSet = dao.readUserBySessionID(sessionID);
                if (userDataSet != null) {
                    final long userID = userDataSet.getId();
                    dao.logoutUser(userID);
                }
                transaction.commit();
            } catch (HibernateException e) {
                if (session.getTransaction().getStatus() == TransactionStatus.ACTIVE
                        || session.getTransaction().getStatus() == TransactionStatus.MARKED_ROLLBACK) {
                    session.getTransaction().rollback();
                }
            }
        }
    }
}
