package main;

import base.*;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import rest.Session;
import rest.Users;
import frontend.*;
import game.GameMechanicsImpl;
import supportclasses.PropertiesReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * MOSch-team test server for "Kill The Birds" game
 */
public class Main {
    private static final Logger LOGGER = LogManager.getLogger(Main.class);

    @SuppressWarnings("OverlyBroadThrowsClause")
    public static void main(String[] args) throws Exception {
        final PropertiesReader serverConfig = new PropertiesReader("server.properties");
        final int port = serverConfig.getPort();

        LOGGER.info("Starting at port: " + String.valueOf(port));

        final Server server = new Server(port);
        final Context ctx = new Context();
        try {
            final AccountServiceImpl accountService = new AccountServiceImpl("hibernate.cfg.xml");
            ctx.put(AccountService.class, accountService);
            ctx.put(WSService.class, new WSServiceImpl());
            ctx.put(GameMechanics.class, new GameMechanicsImpl(ctx.get(WSService.class)));
        } catch (RuntimeException e) {
            LOGGER.error(e);
            System.exit(1);
        }


        final ResourceConfig resourceConfig = new ResourceConfig(Users.class, Session.class);
        resourceConfig.register(new AbstractBinder() {
            @Override
            protected void configure() {
                bind(ctx);
            }
        });

        final ServletContextHandler contextHandler = new ServletContextHandler(server, "/api", ServletContextHandler.SESSIONS);
        final ServletHolder servletHolder = new ServletHolder(new ServletContainer(resourceConfig));
        final HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[]{contextHandler});
        server.setHandler(handlers);
        contextHandler.addServlet(servletHolder, "/*");
        contextHandler.addServlet(new ServletHolder(new WSGameServlet(ctx)), "/gameplay");

        server.start();
        //server.join();
        LOGGER.info("Server started successfully");
        ctx.get(GameMechanics.class).run();
    }
}
