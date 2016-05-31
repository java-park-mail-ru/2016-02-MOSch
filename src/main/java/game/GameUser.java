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

    private int currentHeight = 0;

    private int myLeadCount = 0;

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

    @NotNull
    public int getMyHeight() {
        return currentHeight;
    }

    public Boolean incrementMyLeadCount() {
        myLeadCount++;
        return true;
    }

    public Boolean setHeight(@NotNull int newHeight) {
        currentHeight = newHeight;
        return true;
    }

}
