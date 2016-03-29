package accountService;

import org.eclipse.persistence.internal.sessions.DirectCollectionChangeRecord;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import rest.UserProfile;

import java.io.Serializable;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.Objects;
import java.security.*;
import dbStuff.dataSets.*;
import dbStuff.*;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import accountService.dao.*;

/**
 * MOSch-team test server for "Kill The Birds" game
 */
@SuppressWarnings({"unused"})
public class AccountServiceImpl implements AccountService {
    private final Map<String, UserProfile> users = new ConcurrentHashMap<>();
    private final Map<String, UserProfile> activeUsers = new ConcurrentHashMap<>();
    private SessionFactory sessionFactory;


    public AccountServiceImpl() {
//        users.put("Tolya", new UserProfile("Tolya", "1234567", 1L, UserProfile.RoleEnum.ADMIN));
//        users.put("Kolya", new UserProfile("Kolya", "1234567", 2L));
//        //noinspection MagicNumber
//        users.put("Lesha", new UserProfile("Lesha", "12345", 3L, UserProfile.RoleEnum.ADMIN));

        Configuration configuration = new Configuration();
        configuration.addAnnotatedClass(UserDataSet.class);

        configuration.configure();

        sessionFactory = createSessionFactory(configuration);


    }

    public Collection<UserProfile> getAllUsers() {
        return users.values();
    }

    public Collection<UserProfile> getAllActiveUsers() {
        return activeUsers.values();
    }

    public int countUsers() {
        return users.size();
    }

    public int countActiveUsers() {
        return activeUsers.size();
    }


    public boolean addUser(UserProfile userProfile) {
        UserDataSet dataSet = new UserDataSet(userProfile);
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        UserDataSetDAO dao = new UserDataSetDAO(session);
        Serializable id = dao.save(dataSet);
        transaction.commit();
        if (id != null) {
            dataSet.setId((long) id);
            return true;
        }
        return false;
    }


    public boolean addActiveUser(UserProfile userProfile, String sessionId) {
        final UserProfile user = getUser(userProfile.getLogin()); //due to id-less

        if (activeUsers.containsKey(sessionId)) {
            return false;
        }
        activeUsers.put(sessionId, user);
        return true;
    }

    public void removeActiveUser(String sessionId) {
        activeUsers.remove(sessionId);
    }

    public void removeActiveUser(Long id) {
        this.getAllActiveUsers().stream().filter(user -> Objects.equals(user.getId(), id)).forEach(user -> getAllActiveUsers().remove(user));
    }

    public UserProfile getActiveUser(String sessionId) {
        return activeUsers.get(sessionId);
    }

    public UserProfile getUser(String userName) {
        return users.get(userName);
    }

    public UserDataSet getUserDS(String name) {
        try (Session session = sessionFactory.openSession()) {
            UserDataSetDAO dao = new UserDataSetDAO(session);
            return dao.readByName(name);
        }
    }

    @Nullable
    public UserProfile getUser(Long id) {
        for (UserProfile user : this.getAllUsers()) {
            if (Objects.equals(user.getId(), id))
                return user;
        }
        return null;

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
