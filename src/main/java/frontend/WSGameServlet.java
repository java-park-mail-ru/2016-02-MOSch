package frontend;

import javax.servlet.annotation.WebServlet;

import main.Context;
import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

/**
 * Created by Olerdrive on 29.05.16.
 */
@WebServlet(name = "WebSocketGameServlet", urlPatterns = {"/gameplay"})
public class WSGameServlet {

    private final Context context;
    private static final int IDLE_TIME = 6 * 60 * 1000;

    public WebSocketGameServlet(Context context) {
        this.context = context;
    }

    @Override
    public void configure(WebSocketServletFactory factory) {
        factory.getPolicy().setIdleTimeout(IDLE_TIME);
        factory.setCreator(new GameWebSocketCreator(context));
    }
}
