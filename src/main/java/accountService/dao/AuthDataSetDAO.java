package accountService.dao;

/**
 * Created by Olerdrive on 30.03.16.
 */

import dbStuff.dataSets.*;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import java.io.Serializable;

@SuppressWarnings("unused")
public class AuthDataSetDAO {
    private final Session session;

    public AuthDataSetDAO(Session session) {
        this.session = session;
    }

    public Serializable save(AuthDataSet dataSet) {
        return session.save(dataSet);
    }

    public void saveorupdate(AuthDataSet dataSet) { session.saveOrUpdate(dataSet); }

    public void remove (AuthDataSet dataSet) {session.delete(dataSet);}

    public AuthDataSet getByToken(String token) {
        final Criteria criteria = session.createCriteria(AuthDataSet.class);
        return (AuthDataSet) criteria.add(Restrictions.eq("authToken", token)).uniqueResult();
    }


}
