package rest;

import main.AccountService;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.nio.charset.Charset;
import java.util.Base64;


/**
 * MOSch-team test server for "Kill The Birds" game
 */
@SuppressWarnings("unused")
@Singleton
@Path("/session")
public class Session {
    @Inject
    private main.Context ctx;

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    public Response loginUser(@Context HttpServletRequest request) {
        final AccountService accountService = ctx.get(AccountService.class);

        final String authorization = request.getHeader("Authorization");
        if (authorization != null && authorization.startsWith("Basic ")) {
            final String[] values;
            try {
                final String base64Credentials = authorization.substring("Basic ".length()).trim();
                final String credentials = new String(Base64.getDecoder().decode(base64Credentials), Charset.forName("UTF-8"));

                values = credentials.split(":", 2);
            } catch (IllegalArgumentException e) {
                return Response
                        .status(Response.Status.BAD_REQUEST)
                        .build();
            }

            final String username = values[0];
            final String password = values[1];
            final Long userId = accountService.getUserID(username, password);
            if (userId != null) {
                final HttpSession session = request.getSession(true);
                final String sessionID = session.getId();
                final boolean isLogged = accountService.loginUser(username, password, sessionID);
                if (isLogged) {
                    final String result = String.format("{\"id\":\"%d\"}", userId);
                    return Response
                            .status(Response.Status.OK)
                            .entity(result)
                            .build();
                } else {
                    session.invalidate();
                    final String serverError = "{\"status\":\"500\",\"message\":\"Server Error\"}";
                    return Response
                            .status(Response.Status.INTERNAL_SERVER_ERROR)
                            .entity(serverError)
                            .build();
                }
            } else {
                final String wrong = "{\"message\":\"Wrong login or password\"}";
                return Response
                        .status(Response.Status.FORBIDDEN)
                        .entity(wrong)
                        .build();
            }
        } else {
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .build();
        }
    }

    @DELETE
    public Response logoutUser(@Context HttpServletRequest request) {
        final AccountService accountService = ctx.get(AccountService.class);

        final HttpSession session = request.getSession(false);
        if (session != null) {
            final String sessionID = session.getId();
            accountService.logoutUser(sessionID);
            session.invalidate();
        }
        return Response
                .status(Response.Status.OK)
                .entity("{}")
                .build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response checkAuth(@Context HttpServletRequest request) {
        final AccountService accountService = ctx.get(AccountService.class);

        UserProfile userProfile = null;
        final HttpSession session = request.getSession(false);
        if (session != null) {
            try {
                final String sessionID = session.getId();
                userProfile = accountService.getUserBySessionID(sessionID);
            } catch (IllegalStateException e) {
                userProfile = null;
            }
        }
        if (userProfile == null) {
            return Response
                    .status(Response.Status.UNAUTHORIZED)
                    .build();
        } else {
            final String result = String.format("{\"id\":\"%d\"}", userProfile.getId());
            return Response
                    .status(Response.Status.OK)
                    .entity(result)
                    .build();
        }
    }
}
