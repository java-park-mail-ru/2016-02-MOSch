package accountService.dao;

/**
 * Created by Olerdrive on 29.03.16.
 */

import dbStuff.dataSets.UserDataSet;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
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

    public UserDataSet getUser(long id) {
        return (UserDataSet) session.get(UserDataSet.class, id);
    }

    public void saveorupdate(UserDataSet dataSet) {
        session.saveOrUpdate(dataSet);
    }

    public void remove(UserDataSet dataSet) {
        session.delete(dataSet);
    }

    public UserDataSet readByName(String login) {
        Criteria criteria = session.createCriteria(UserDataSet.class);
        return (UserDataSet) criteria.add(Restrictions.eq("login", login)).uniqueResult();
    }

    @SuppressWarnings("unchecked")
    public List readAll() {
        Projection id = Projections.property("id");
        Projection login = Projections.property("login");
        Criteria criteria = session.createCriteria(UserDataSet.class).setProjection(id);
        criteria.setProjection(login);
        return criteria.list();
    }

    public Long countUsers() {
        return (Long) session.createCriteria(UserDataSet.class).setProjection(Projections.rowCount()).uniqueResult();
    }
}
