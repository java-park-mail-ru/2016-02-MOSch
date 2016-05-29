package game;


import org.jetbrains.annotations.NotNull;

import java.time.Clock;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Olerdrive on 29.05.16.
 */
public class GameSession {
    private final long startTime;
    @NotNull
    private final GameUser first;
    @NotNull
    private final GameUser second;

    @NotNull
    private final Map<String, GameUser> users = new HashMap<>();

    public GameSession(@NotNull String user1, @NotNull String user2) {
        startTime = Clock.systemDefaultZone().millis();
        final GameUser gameUser1 = new GameUser(user1, user2);

        final GameUser gameUser2 = new GameUser(user2, user1);

        users.put(user1, gameUser1);
        users.put(user2, gameUser2);

        this.first = gameUser1;
        this.second = gameUser2;
    }

    @NotNull
    public GameUser getEnemy(@NotNull String user) {
        final String enemyName = users.containsKey(user) ? users.get(user).getEnemyName() : null;
        return users.get(enemyName);
    }

    @NotNull
    public GameUser getSelf(String user) {
        return users.get(user);
    }

    public int getEnemyHeight(@NotNull String user) {
        final String enemyName = users.containsKey(user) ? users.get(user).getEnemyName() : null;
        return users.get(enemyName).getMyHeight();
    }

    public int getMyHeight(@NotNull String user) {
        return users.get(user).getMyHeight();
    }

    public long getSessionTime() {
        return Clock.systemDefaultZone().millis() - startTime;
    }

    @NotNull
    public GameUser getFirst() {
        return first;
    }

    @NotNull
    public GameUser getSecond() {
        return second;
    }

    public boolean isFirstWin() {
        return first.getMyHeight() > second.getMyHeight();
    }

    public boolean isEquality() {
        return first.getMyHeight() == second.getMyHeight();
    }
}
