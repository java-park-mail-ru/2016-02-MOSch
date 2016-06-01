package game;

import base.GameMechanics;
import base.WSService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import supportclasses.TimeHelper;

import java.util.*;

/**
 * Created by Olerdrive on 29.05.16.
 */
public class GameMechanicsImpl implements GameMechanics {

    private static final int STEP_TIME = 100;

    private static final Logger LOGGER = LogManager.getLogger(GameMechanicsImpl.class);

    @NotNull
    private final WSService webSocketService;

    @NotNull
    private final Map<String, GameSession> nameToGame = new HashMap<>();

    @NotNull
    private final Set<GameSession> allSessions = new HashSet<>();

    @Nullable
    private volatile String waiter;

    public GameMechanicsImpl(@NotNull WSService webSocketService) {
        this.webSocketService = webSocketService;

    }

    @Override
    public GameSession getGameSession(@NotNull String name) {
        return nameToGame.get(name);
    }

    @Override
    public int getMyScore(String user) {
        return nameToGame.get(user).getMyHeight(user);
    }

    @Override
    public Integer getMyLeadCount(String user) {
        return nameToGame.get(user).getSelf(user).getMyLeadCount();
    }

    @Override
    public int getEnemyScore(String user) {
        return nameToGame.get(user).getEnemy(user).getMyHeight();
    }

    @Override
    public String getEnemyName(String user) {
        return nameToGame.get(user).getEnemy(user).getMyName();
    }

    @Override
    public Integer getEnemyLeadCount(String user) {
        return nameToGame.get(user).getEnemy(user).getMyLeadCount();
    }

    @Override
    public void addUser(@NotNull String user) {
        if (waiter != null) {
            //noinspection ConstantConditions
            starGame(user, waiter);
            waiter = null;
        } else {
            waiter = user;
        }
    }

    @Override
    public void removeGameSession(@NotNull String user) {
        allSessions.remove(nameToGame.get(user));
        nameToGame.remove(user);
    }

    @Override
    public void removeUser(@NotNull String user) {
        if (waiter != null && user.equals(waiter)) {
            waiter = null;
        }
        final GameSession myGameSession = nameToGame.get(user);
        if (myGameSession != null) {
            final GameUser enemyUser = myGameSession.getEnemy(user);
            webSocketService.notifyEnemyLeft(enemyUser);
        }
    }

    @Override
    public void incrementScore(String userName) {
        final GameSession myGameSession = nameToGame.get(userName);
        final GameUser myUser = myGameSession.getSelf(userName);
        final int newHeight = myUser.getMyHeight() + 1;
        myUser.setHeight(newHeight);

        if (newHeight > myGameSession.getEnemy(userName).getMyHeight())
            myUser.incrementMyLeadCount();
    }

    @Override
    public void setLooser(String userName) {
        final GameSession myGameSession = nameToGame.get(userName);
        final GameUser myUser = myGameSession.getSelf(userName);
        myUser.setLooser();
    }


    @Override
    public void run() {
        LOGGER.info("Gamemechanics loop started successfully");
        //noinspection InfiniteLoopStatement
        while (true) {
            gmStep();
            TimeHelper.sleep(STEP_TIME);
        }
    }

    @Override
    public void gmStep() {
        allSessions.stream().filter(GameSession::isGameOver).forEach(session -> {
            LOGGER.info("Game is over for " + session.getFirst().getMyName() + " and " + session.getSecond().getMyName());
            if (session.isEquality()) {
                webSocketService.notifyGameOver(session.getFirst(), true, false);
                webSocketService.notifyGameOver(session.getSecond(), true, false);
            } else {
                final boolean firstWin = session.isFirstWin();
                webSocketService.notifyGameOver(session.getFirst(), false, firstWin);
                webSocketService.notifyGameOver(session.getSecond(), false, !firstWin);
            }
            nameToGame.values().removeAll(Collections.singleton(session));
            allSessions.remove(session);
        });
    }

    @Override
    public void starGame(@NotNull String first, @NotNull String second) {
        final GameSession gameSession = new GameSession(first, second);
        allSessions.add(gameSession);
        nameToGame.put(first, gameSession);
        nameToGame.put(second, gameSession);

        webSocketService.notifyStartGame(gameSession.getSelf(first));
        webSocketService.notifyStartGame(gameSession.getSelf(second));
    }

}
