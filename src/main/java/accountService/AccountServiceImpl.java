package accountService;

import accountService.dao.AuthDataSetDAO;
import accountService.dao.ScoreDataSetDAO;
import accountService.dao.UserDataSetDAO;
import dbStuff.AccountService;
import dbStuff.dataSets.AuthDataSet;
import dbStuff.dataSets.ScoreDataSet;
import dbStuff.dataSets.UserDataSet;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import rest.UserProfile;

import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * MOSch-team test server for "Kill The Birds" game
 */
@SuppressWarnings({"unused", "MismatchedQueryAndUpdateOfCollection"})
public class AccountServiceImpl implements AccountService {
    private final Map<String, UserProfile> users = new ConcurrentHashMap<>();
    private final Map<String, UserProfile> activeUsers = new ConcurrentHashMap<>();
    final SessionFactory sessionFactory;

    public AccountServiceImpl() {
        final Configuration configuration = new Configuration();
        configuration.addAnnotatedClass(UserDataSet.class);
        configuration.addAnnotatedClass(AuthDataSet.class);
        configuration.addAnnotatedClass(ScoreDataSet.class);
        configuration.configure("hibernate.cfg.xml");
        sessionFactory = createSessionFactory(configuration);
    }

    @Override
    public Collection getAllUsers() {
        final Session session = sessionFactory.openSession();
        final UserDataSetDAO userdao = new UserDataSetDAO(session);
        final List list = userdao.readAll();
        session.close();
        return list;
    }

    @Override
    public Collection<UserProfile> getAllActiveUsers() {
        return activeUsers.values();
    }

    @Override
    public Long countUsers() {
        //noinspection HibernateResourceOpenedButNotSafelyClosed
        final Session session = sessionFactory.openSession();
        final Long result = new UserDataSetDAO(session).countUsers();
        session.close();
        return result;

    }

    @Override
    public Long countActiveUsers() {
        return (long) activeUsers.size();
    }

    @Override
    public boolean addUser(UserProfile userProfile) {
        final UserDataSet dataSet = new UserDataSet(userProfile);
        final Session session = sessionFactory.openSession();
        final UserDataSetDAO userdao = new UserDataSetDAO(session);
        final Serializable id = userdao.save(dataSet);

        if (id != null) {
            dataSet.setId((long) id);
            final ScoreDataSet scoreDS = new ScoreDataSet((long) id);
            final ScoreDataSetDAO scoredao = new ScoreDataSetDAO(session);
            scoredao.save(scoreDS);
            session.close();
            return true;
        }
        session.close();
        return false;
    }

    @Override
    public void addActiveUser(UserDataSet userDS, String authToken) {
        final AuthDataSet authDS = new AuthDataSet(userDS);
        authDS.setToken(authToken);
        final Session session = sessionFactory.openSession();
        final AuthDataSetDAO authdao = new AuthDataSetDAO(session);
        authdao.saveorupdate(authDS);
        session.close();
    }

    @Override
    public void removeActiveUser(String authToken) {
        final Session session = sessionFactory.openSession();
        final AuthDataSetDAO authdao = new AuthDataSetDAO(session);
        final AuthDataSet authDS = authdao.getByToken(authToken);
        if (authDS != null)
            authdao.remove(authDS);
        session.close();
    }

    @Override
    public void removeActiveUser(Long id) {
        this.getAllActiveUsers().stream().filter(user -> Objects.equals(user.getId(), id)).forEach(user -> getAllActiveUsers().remove(user));
    }

    @Override
    public AuthDataSet getActiveUser(String currentToken) {
        final Session session = sessionFactory.openSession();
        final AuthDataSetDAO authdao = new AuthDataSetDAO(session);
        final AuthDataSet authDS = authdao.getByToken(currentToken);
        session.close();
        return authDS;
    }

    @Override
    public void removeUser(UserDataSet userDS) {
        final Session session = sessionFactory.openSession();
        final UserDataSetDAO userdao = new UserDataSetDAO(session);
        userdao.remove(userDS);
        session.close();
    }


    @Override
    public void updateUser(UserDataSet userDS) {
        final Session session = sessionFactory.openSession();
        final UserDataSetDAO userdao = new UserDataSetDAO(session);
        userdao.saveorupdate(userDS);
        session.close();
    }

    @Override
    public UserDataSet getUserDS(String name) {
        final Session session = sessionFactory.openSession();
        final UserDataSetDAO dao = new UserDataSetDAO(session);
        final UserDataSet userDataSet = dao.readByName(name);
        session.close();
        return userDataSet;
    }

    @Override
    @Nullable
    public UserDataSet getUser(Long id) {
        final Session session = sessionFactory.openSession();
        final UserDataSetDAO userdao = new UserDataSetDAO(session);
        final UserDataSet userDataSet = userdao.getUser(id);
        session.close();
        return userDataSet;
    }


    @SuppressWarnings("MagicNumber")
    @NotNull
    public static String getMD5(String input) {
        try {
            final MessageDigest md = MessageDigest.getInstance("MD5");
            final byte[] messageDigest = md.digest(input.getBytes());
            // Convert to hex string
            final StringBuilder sb = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                if ((0xff & aMessageDigest) < 0x10) {
                    sb.append('0');
                }
                sb.append(Integer.toHexString(0xff & aMessageDigest));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private static SessionFactory createSessionFactory(Configuration configuration) {
        final StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder();
        builder.applySettings(configuration.getProperties());
        final ServiceRegistry serviceRegistry = builder.build();
        return configuration.buildSessionFactory(serviceRegistry);
    }
}
