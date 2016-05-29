package supportclasses;

import db.datasets.UserDataSet;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Olerdrive on 06.04.16.
 */
public class LoginScoreSet {
    @NotNull
    private final Long id;
    @NotNull
    private final String username;
    @NotNull
    private final Long score;
    @NotNull
    private final Boolean star_bf;

    public LoginScoreSet(@NotNull UserDataSet uDS) {
        this.id = uDS.getId();
        this.username = uDS.getUsername();
        this.score = uDS.getScore();
        this.star_bf = uDS.getStarBf();
    }
}
