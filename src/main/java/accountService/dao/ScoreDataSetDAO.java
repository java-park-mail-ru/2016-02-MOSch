package accountService.dao;

/**
 * Created by Olerdrive on 30.03.16.
 */
import dbStuff.dataSets.*;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import java.io.Serializable;
import java.util.List;


public class ScoreDataSetDAO {
    private Session session;

    public ScoreDataSetDAO(Session session) {
        this.session = session;
    }

    public Serializable save(ScoreDataSet dataSet) {
        return session.save(dataSet);
    }
}
