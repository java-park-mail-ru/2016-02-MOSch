package db.datasets;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import supportclasses.*;

import java.util.ArrayList;
import java.util.List;

/**
 * MOSch-team test server for "Kill The Birds" game
 */
public class UserDataSetDAO {
    private final Session session;

    public UserDataSetDAO(Session session) {
        this.session = session;
    }

    // true
    public void updateUser(@NotNull Long userID, @NotNull UserDataSet dataSet) {
        final UserDataSet oldDataSet = readUserByID(userID);
        if (oldDataSet != null) {
            if (oldDataSet.getRate() < dataSet.getRate()) {
                oldDataSet.setRate(dataSet.getRate());
                oldDataSet.setLevel(dataSet.getLevel());
            }
        }
        session.flush();
    }

    // true
    public void deleteUser(long userID) {
        final UserDataSet user = readUserByID(userID);
        if (user != null) {
            session.delete(user);
        }
        session.flush();
    }

    // true
    public boolean createUser(@NotNull UserDataSet dataSet) {
        return (session.save(dataSet) != null);
    }

    // true
    public List<UserDataSet> readAll() {
        final Criteria criteria = session.createCriteria(UserDataSet.class);

        //noinspection unchecked
        return (List<UserDataSet>) criteria.list();
    }


    public List<LoginScoreSet> readTop() {
        final Criteria criteria = session.createCriteria(UserDataSet.class);
        criteria.addOrder(Order.desc("rate"));
        criteria.addOrder(Order.asc("username"));
        final List dsList = criteria.list();

        final ArrayList<LoginScoreSet> result = new ArrayList<>(dsList.size());

        for (Object uDS : dsList) {
            result.add(new LoginScoreSet((UserDataSet) uDS));
        }
        return result;

    }

    // true
    public long countUsers() {
        return (long) session.createCriteria(UserDataSet.class).setProjection(Projections.rowCount()).uniqueResult();
    }

    // true
    @Nullable
    public UserDataSet readUserByID(@NotNull Long id) {
        final Criteria criteria = session.createCriteria(UserDataSet.class);
        return (UserDataSet) criteria.add(Restrictions.eq("id", id)).uniqueResult();
    }

    // true
    @Nullable
    public UserDataSet readUserByLogin(@NotNull String login) {
        final Criteria criteria = session.createCriteria(UserDataSet.class);
        return (UserDataSet) criteria.add(Restrictions.eq("username", login)).uniqueResult();
    }


//    @Nullable
//    public UserDataSet getUserBySession(String sessionID) {
//        TODO
//    }
}
