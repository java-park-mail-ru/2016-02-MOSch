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
    private final Long rate;

    @NotNull
    private final Long level;


    public LoginScoreSet(@NotNull UserDataSet uDS){
        this.id = uDS.getId();
        this.username = uDS.getUsername();
        this.rate = uDS.getRate();
        this.level = uDS.getLevel();
    }

    @Override
    public String toString() {
        return "id=" + id + ", username=" + username + ", rate="
                + rate + ", level=" + level;
    }
}
