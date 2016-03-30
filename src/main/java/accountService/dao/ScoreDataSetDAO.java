package accountService.dao;

/**
 * Created by Olerdrive on 30.03.16.
 */
import dbStuff.dataSets.*;
import org.hibernate.Session;


public class ScoreDataSetDAO {
    private final Session session;

    public ScoreDataSetDAO(Session session) {
        this.session = session;
    }

    public void save(ScoreDataSet dataSet) {
        session.save(dataSet);
    }
}
