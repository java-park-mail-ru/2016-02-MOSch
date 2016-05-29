package base;

import frontend.GameWS;
import game.GameUser;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Olerdrive on 29.05.16.
 */
@SuppressWarnings("unused")
public interface WSService {
    void addUser(GameWS user);

    void notifyStartGame(GameUser user);

    void notifyGameOver(GameUser user, boolean equality, boolean win);

    void removeUser(@NotNull GameWS user);

    void notifyEnemyLeft(GameUser user);
