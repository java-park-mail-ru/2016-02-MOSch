package main;

import base.AccountService;
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
import frontend.WSGameServlet;
import game.GameMechanicsImpl;

/**
 * MOSch-team test server for "Kill The Birds" game
 */
public class Main {

    public static final int DEFAULT_PORT = 8080;

    @SuppressWarnings("OverlyBroadThrowsClause")
    public static void main(String[] args) throws Exception {
        int port = DEFAULT_PORT;
        if (args.length == 1) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.err.println("Specify correct port");
                System.exit(1);
            }
        } else {
            System.err.println("Specify port");
            System.err.println("Uses the default port 8080");
        }

        System.out.append("Starting at port: ").append(String.valueOf(port)).append('\n');

        final Server server = new Server(port);
        final Context ctx = new Context();
        try {
            final AccountServiceImpl accountService = new AccountServiceImpl("hibernate.cfg.xml");
            ctx.put(AccountService.class, accountService);
        } catch (RuntimeException e) {
            System.err.println(e.getLocalizedMessage());
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
        ctx.get(GameMechanicsImpl.class).run();
    }
}
