package supportclasses;

import db.datasets.UserDataSet;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Olerdrive on 06.04.16.
 */
@SuppressWarnings("unused")
public class LoginScoreSet {
    @NotNull
    public final Long id;
    @NotNull
    public final String username;
    @NotNull
    public final Long score;
    @NotNull
    public final Boolean star_bf;

    public LoginScoreSet(@NotNull UserDataSet uDS) {
        this.id = uDS.getId();
        this.username = uDS.getUsername();
        this.score = uDS.getScore();
        this.star_bf = uDS.getStarBf();
    }
}
