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

    public GameUser(@NotNull String myName) {
        this.myName = myName;
    }

    @NotNull
    public String getMyName() {
        return myName;
    }

    @NotNull
    public int getMyHeight(@NotNull String myName){
        return currentHeight;
    }

    public Boolean incrementMeLeadCount(){
        myLeadCount++;
        return true;
    }

    public Boolean setHeight(@NotNull int newHeight){
        currentHeight = newHeight;
        return true;
    }

}
