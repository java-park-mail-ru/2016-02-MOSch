package base;
import org.jetbrains.annotations.NotNull;
import game.GameSession;

/**
 * Created by Olerdrive on 29.05.16.
 */
public interface GameMechanics {

    int getMyScore(String user);

    int getEnemyScore(String user);

    void addUser(@NotNull String user);

    void removeUser(@NotNull String user);

    void incrementScore(String userName);

    void setLooser(String userName);

    void removeGameSession(@NotNull String user);

    Integer getEnemyLeadCount(String user);

    String getEnemyName(String user);

    Integer getMyLeadCount(String user);

    GameSession getGameSession(@NotNull String name);



    void run();

    void starGame(@NotNull String first, @NotNull String second);

    void gmStep();
}
