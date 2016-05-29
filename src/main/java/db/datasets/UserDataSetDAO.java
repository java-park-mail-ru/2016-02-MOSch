package db.datasets;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import supportclasses.LoginScoreSet;

import java.util.ArrayList;
import java.util.List;

/**
 * MOSch-team test server for "Kill The Birds" game
 */
public class UserDataSetDAO {
    private static final Long DEFAULT_MULTIPLIER = 1000L;
    private final Session session;

    public UserDataSetDAO(Session session) {
        this.session = session;
    }

    public static void logoutAll(Session session) {
        final List dsList = session.createCriteria(UserDataSet.class).list();
        for (Object uDS : dsList) {
            ((UserDataSet) uDS).setAuthToken(null);
        }
    }

    public static void deleteAll(Session session) {
        final List dsList = session.createCriteria(UserDataSet.class).list();
        //noinspection unchecked
        dsList.forEach(session::delete);
    }

    public boolean createUser(@NotNull UserDataSet dataSet) {
        return (session.save(dataSet) != null);
    }

    public void deleteUser(@NotNull Long userID) {
        final UserDataSet user = readUserByID(userID);
        if (user != null) {
            session.delete(user);
        }
    }

    @Nullable
    public UserDataSet readUserByID(@NotNull Long userID) {
        final Criteria criteria = session.createCriteria(UserDataSet.class);
        return (UserDataSet) criteria.add(Restrictions.eq("id", userID)).uniqueResult();
    }

    @Nullable
    public UserDataSet readUserBySessionID(@NotNull String sessionID) {
        final Criteria criteria = session.createCriteria(UserDataSet.class);
        return (UserDataSet) criteria.add(Restrictions.eq("authToken", sessionID)).uniqueResult();
    }

    @Nullable
    public UserDataSet readUserByLogin(@NotNull String username) {
        final Criteria criteria = session.createCriteria(UserDataSet.class);
        return (UserDataSet) criteria.add(Restrictions.eq("username", username)).uniqueResult();
    }

    public void updateUser(@NotNull Long userID, @NotNull UserDataSet dataSet, @SuppressWarnings("SameParameterValue") @Nullable Long multiplier) {
        if (multiplier == null) {
            multiplier = DEFAULT_MULTIPLIER;
        }
        final UserDataSet oldDataSet = readUserByID(userID);
        if (oldDataSet != null) {
            final Long newScore = dataSet.getScore();
            if (oldDataSet.getScore() < newScore) {
                oldDataSet.setScore(newScore);
            }
            oldDataSet.setPoints(oldDataSet.getPoints() + multiplier * newScore);
        }
    }

    public long countUsers() {
        return (long) session.createCriteria(UserDataSet.class).setProjection(Projections.rowCount()).uniqueResult();
    }

    @NotNull
    public List<LoginScoreSet> readAll() {
        final Criteria criteria = session.createCriteria(UserDataSet.class);
        criteria.addOrder(Order.desc("score"));
        criteria.addOrder(Order.asc("username"));
        final List dsList = criteria.list();
        final ArrayList<LoginScoreSet> result = new ArrayList<>(dsList.size());
        for (Object uDS : dsList) {
            result.add(new LoginScoreSet((UserDataSet) uDS));
        }
        return result;
    }

    @Nullable
    public String loginUser(@NotNull Long userID, @NotNull String sessionID) {
        final UserDataSet userDataSet = readUserByID(userID);
        if (userDataSet != null) {
            userDataSet.setAuthToken(sessionID);
            return sessionID;
        } else {
            return null;
        }
    }

    public void logoutUser(@NotNull Long userID) {
        final UserDataSet userDataSet = readUserByID(userID);
        if (userDataSet != null) {
            userDataSet.setAuthToken(null);
        }
    }
}
