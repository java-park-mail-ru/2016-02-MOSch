package game;

import org.jetbrains.annotations.NotNull;


/**
 * Created by Olerdrive on 29.05.16.
 */
public class GameUser {
    @NotNull
    private final String myName;

    @NotNull
    private String enemyName;

    private int myHeight = 0;
    private int enemyHeight = 0;

    private int myLeadCount = 0;
    private int enemyLeadCount = 0;

    public GameUser(@NotNull String myName, @NotNull String enemyName) {

        this.myName = myName;
        this.enemyName = enemyName;
    }

    @NotNull
    public String getMyName() {
        return myName;
    }
    
    @NotNull
    public String getEnemyName() {
        return enemyName;
    }

}
