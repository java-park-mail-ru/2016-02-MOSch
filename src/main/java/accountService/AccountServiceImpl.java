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
@SuppressWarnings({"unused"})
public class AccountServiceImpl implements AccountService {
    private final Map<String, UserProfile> users = new ConcurrentHashMap<>();
    private final Map<String, UserProfile> activeUsers = new ConcurrentHashMap<>();
    SessionFactory sessionFactory;

    public AccountServiceImpl() {
        Configuration configuration = new Configuration();
        configuration.addAnnotatedClass(UserDataSet.class);
        configuration.addAnnotatedClass(AuthDataSet.class);
        configuration.addAnnotatedClass(ScoreDataSet.class);
        configuration.configure("hibernate.cfg.xml");
        sessionFactory = createSessionFactory(configuration);
    }

    public Collection getAllUsers() {
        Session session = sessionFactory.openSession();
        UserDataSetDAO userdao = new UserDataSetDAO(session);
        List list = userdao.readAll();
        session.close();
        return list;
    }

    public Collection<UserProfile> getAllActiveUsers() {
        return activeUsers.values();
    }

    public Long countUsers() {
        Session session = sessionFactory.openSession();
        Long result = new UserDataSetDAO(session).countUsers();
        session.close();
        return result;
    }

    public Long countActiveUsers() {
        return new Long(activeUsers.size());
    }

    public boolean addUser(UserProfile userProfile) {
        UserDataSet dataSet = new UserDataSet(userProfile);
        Session session = sessionFactory.openSession();
        UserDataSetDAO userdao = new UserDataSetDAO(session);
        Serializable id = userdao.save(dataSet);

        if (id != null) {
            dataSet.setId((long) id);
            ScoreDataSet scoreDS = new ScoreDataSet((long) id);
            ScoreDataSetDAO scoredao = new ScoreDataSetDAO(session);
            scoredao.save(scoreDS);
            session.close();
            return true;
        }
        session.close();
        return false;
    }

    public boolean addActiveUser(UserDataSet userDS, String auth_token) {
        AuthDataSet authDS = new AuthDataSet(userDS);
        authDS.setToken(auth_token);
        Session session = sessionFactory.openSession();
        AuthDataSetDAO authdao = new AuthDataSetDAO(session);
        authdao.saveorupdate(authDS);
        session.close();
        return true;
    }

    public void removeActiveUser(String authToken) {
        Session session = sessionFactory.openSession();
        AuthDataSetDAO authdao = new AuthDataSetDAO(session);
        AuthDataSet authDS = authdao.getByToken(authToken);
        if (authDS != null)
            authdao.remove(authDS);
        session.close();
    }

    public void removeActiveUser(Long id) {
        this.getAllActiveUsers().stream().filter(user -> Objects.equals(user.getId(), id)).forEach(user -> getAllActiveUsers().remove(user));
    }

    public AuthDataSet getActiveUser(String currentToken) {
        Session session = sessionFactory.openSession();
        AuthDataSetDAO authdao = new AuthDataSetDAO(session);
        AuthDataSet authDS = authdao.getByToken(currentToken);
        session.close();
        return authDS;
    }

    public void removeUser(UserDataSet userDS) {
        Session session = sessionFactory.openSession();
        UserDataSetDAO userdao = new UserDataSetDAO(session);
        userdao.remove(userDS);
        session.close();
    }


    public void updateUser(UserDataSet userDS) {
        Session session = sessionFactory.openSession();
        UserDataSetDAO userdao = new UserDataSetDAO(session);
        userdao.saveorupdate(userDS);
        session.close();
    }

    public UserDataSet getUserDS(String name) {
        Session session = sessionFactory.openSession();
        UserDataSetDAO dao = new UserDataSetDAO(session);
        UserDataSet userDataSet = dao.readByName(name);
        session.close();
        return userDataSet;
    }

    @Nullable
    public UserDataSet getUser(Long id) {
        Session session = sessionFactory.openSession();
        UserDataSetDAO userdao = new UserDataSetDAO(session);
        UserDataSet userDataSet = userdao.getUser(id);
        session.close();
        return userDataSet;
    }

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
        StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder();
        builder.applySettings(configuration.getProperties());
        ServiceRegistry serviceRegistry = builder.build();
        return configuration.buildSessionFactory(serviceRegistry);
    }
}