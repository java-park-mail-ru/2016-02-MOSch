package db.datasets;

import org.hibernate.Session;

/**
 * MOSch-team test server for "Kill The Birds" game
 */
public class ScoreDataSetDAO {
    private final Session session;

    public ScoreDataSetDAO(Session session) {
        this.session = session;
    }

    public void save(ScoreDataSet dataSet) {
        session.save(dataSet);
    }
}
