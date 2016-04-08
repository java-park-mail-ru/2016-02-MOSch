package oldDB;

import oldDB.AuthDataSet_old;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import java.io.Serializable;

/**
 * MOSch-team test server for "Kill The Birds" game
 */
@SuppressWarnings("unused")
public class AuthDataSetDAO_old {
    private final Session session;

    public AuthDataSetDAO_old(Session session) {
        this.session = session;
    }

    public Serializable save(AuthDataSet_old dataSet) {
        return session.save(dataSet);
    }

    public void saveorupdate(AuthDataSet_old dataSet) {
        session.saveOrUpdate(dataSet);
    }

    public void remove(AuthDataSet_old dataSet) {
        session.delete(dataSet);
    }

    public AuthDataSet_old getByToken(String token) {
        final Criteria criteria = session.createCriteria(AuthDataSet_old.class);
        return (AuthDataSet_old) criteria.add(Restrictions.eq("authToken", token)).uniqueResult();
    }


}
