package supportClasses;

import org.jetbrains.annotations.NotNull;
import org.mockito.internal.matchers.Not;

import java.util.List;

/**
 * Created by Olerdrive on 06.04.16.
 */
public class LoginScoreSet {
    @NotNull
    private Long id;

    @NotNull
    private String username;

    @NotNull
    private Long scores;


    public LoginScoreSet(){
        id = -1L;
        username = "";
        scores = 0L;
    }

    public LoginScoreSet(@NotNull Long id, @NotNull String username, @NotNull Long scores){
        this.id = id;
        this.username = username;
        this.scores = scores;
    }
}
