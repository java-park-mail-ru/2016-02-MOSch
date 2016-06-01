package frontend;

import rest.UserProfile;
import base.AccountService;
import main.Context;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeRequest;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeResponse;
import org.eclipse.jetty.websocket.servlet.WebSocketCreator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

/**
 * Created by Olerdrive on 29.05.16.
 */
public class GameWSCreator implements WebSocketCreator {

    private static final Logger LOGGER = LogManager.getLogger(GameWSCreator.class);

    private final Context context;

    public GameWSCreator(Context context) {
        this.context = context;
    }

    @Override
    @Nullable
    public GameWS createWebSocket(ServletUpgradeRequest req, ServletUpgradeResponse resp) {

        final String sessionId = req.getHttpServletRequest().getSession().getId();
        final AccountService accountService = context.get(AccountService.class);
        final UserProfile userBySession = accountService.getUserBySessionID(sessionId);

        if (userBySession == null) {
            LOGGER.error("Can't create websocket, user is not logged in");
            return null;
        } else {
            return new GameWS(userBySession.getUsername(), context);
        }
    }
}