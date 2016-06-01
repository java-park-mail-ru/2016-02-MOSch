package frontend;

import game.GameUser;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import base.WSService;


/**
 * Created by Olerdrive on 29.05.16.
 */
public class WSServiceImpl implements WSService{
    private final Map<String, GameWS> userSockets = new HashMap<>();

    @Override
    public void addUser(GameWS user) {
        userSockets.put(user.getMyName(), user);
    }

    @Override
    public void notifyStartGame(GameUser user) {
        final GameWS gameWebSocket = userSockets.get(user.getMyName());
        gameWebSocket.startGame(user);
    }

    @Override
    public void notifyGameOver(GameUser user, boolean equality, boolean firstWinBF) {
        if (equality) {
            userSockets.get(user.getMyName()).gameOver(true, false);
        } else {
            userSockets.get(user.getMyName()).gameOver(false, firstWinBF);
        }

        userSockets.remove(user.getMyName());
    }

    @Override
    public void removeUser(@NotNull GameWS user) {
        userSockets.remove(user.getMyName());
    }

    @Override
    public void notifyEnemyLeft(GameUser user) {
        final GameWS gameWebSocket = userSockets.get(user.getMyName());
        if (gameWebSocket != null) {
            gameWebSocket.finishGameEnemyleft();
        }
    }
}
