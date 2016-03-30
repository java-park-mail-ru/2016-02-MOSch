package accountService.dao;

/**
 * Created by Olerdrive on 29.03.16.
 */
import dbStuff.dataSets.UserDataSet;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import java.io.Serializable;
import java.util.List;

public class UserDataSetDAO {
    private Session session;

    public UserDataSetDAO(Session session) {
        this.session = session;
    }

    public Serializable save(UserDataSet dataSet) {
        return session.save(dataSet);
    }

    public UserDataSet read(long id) {
        return (UserDataSet) session.get(UserDataSet.class, id);
    }

    public UserDataSet readByName(String login) {
        Criteria criteria = session.createCriteria(UserDataSet.class);
        return (UserDataSet) criteria.add(Restrictions.eq("login", login)).uniqueResult();
    }

    @SuppressWarnings("unchecked")
    public List<UserDataSet> readAll() {
        Criteria criteria = session.createCriteria(UserDataSet.class);
        return (List<UserDataSet>) criteria.list();
    }


}