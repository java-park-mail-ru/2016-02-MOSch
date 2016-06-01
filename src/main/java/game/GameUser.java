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

    private boolean isLooser = false;

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
    public Integer getMyHeight() {
        return currentHeight;
    }

    @NotNull
    public Integer getMyLeadCount(){
        return myLeadCount;
    }

    public void incrementMyLeadCount() {
        myLeadCount++;
    }

    public void setHeight(@NotNull Integer newHeight) {
        currentHeight = newHeight;
    }

    public boolean isLooser(){
        return isLooser;
    }

    public void setLooser(){
        this.isLooser = true;
    }

}
