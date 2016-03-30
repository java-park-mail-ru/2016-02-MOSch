package main;

import accountService.AccountServiceImpl;
import dbStuff.AccountService;
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

/**
 * MOSch-team test server for "Kill The Birds" game
 */
public class Main {

    public static void main(String[] args) throws Exception {
        int port = -1;
        if (args.length == 1) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.err.println("Specify correct port");
                System.exit(1);
            }
        } else {
            System.err.println("Specify port");
            System.exit(1);
        }

        System.out.append("Starting at port: ").append(String.valueOf(port)).append('\n');

        final Server server = new Server(port);
        final Context ctx = new Context();
        try {
            AccountServiceImpl accountService = new AccountServiceImpl();
            ctx.put(AccountService.class, accountService);
        } catch (Exception e) {
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

        final ServletContextHandler contextHandler = new ServletContextHandler(server, "/api/", ServletContextHandler.SESSIONS);
        final ServletHolder servletHolder = new ServletHolder(new ServletContainer(resourceConfig));
        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[]{contextHandler});
        server.setHandler(handlers);
        contextHandler.addServlet(servletHolder, "/*");

        server.start();
        server.join();
    }
}
