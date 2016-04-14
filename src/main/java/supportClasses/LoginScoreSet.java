package supportClasses;

import db.datasets.UserDataSet;
import org.jetbrains.annotations.NotNull;
import org.mockito.internal.matchers.Not;

import java.util.List;

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


    public LoginScoreSet(){
        id = -1L;
        username = "";
        rate = 0L;
        level = 0L;
    }

    public LoginScoreSet(@NotNull Long id, @NotNull String username, @NotNull Long rate, @NotNull Long level){
        this.id = id;
        this.username = username;
        this.rate = rate;
        this.level = level;
    }

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
