package base;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Olerdrive on 29.05.16.
 */
public interface GameMechanicsService {

    int getMyScore(String user);

    int getEnemyScore(String user);

    void addUser(@NotNull String user);

    void removeUser(@NotNull String user);

    void incrementScore(String userName);

    void run();

    void starGame(@NotNull String first, @NotNull String second);

    void gmStep();
}
