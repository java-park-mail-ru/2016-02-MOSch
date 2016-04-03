package rest;

import main.AccountService;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collection;
import javax.ws.rs.core.Response;
import java.io.IOException;
import rest.UserProfile;

import javax.ws.rs.core.MediaType.*;

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
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response loginUser(UserProfile user, @Context HttpServletRequest request,
                              @HeaderParam("auth_token") String currentToken)
            throws IOException {

        final AccountService accountService = ctx.get(AccountService.class);
        final String authToken;
        if (currentToken != null) {
            authToken = accountService.loginUser(currentToken);
        } else if (user != null) {
            authToken = accountService.loginUser(user.getLogin(), user.getPassword());
        } else {
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .build();
        }
        if (authToken != null) {
            final UserProfile userProfile = accountService.getUserBySessionID(authToken);
            if (userProfile == null) {
                final String serverError = "{\"status\":\"500\",\"message\":\"Server Error\"}";
                return Response
                        .status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity(serverError)
                        .build();
            } else {
                final String result = String.format("{\"id\":\"%d\", \"auth_token\":\"%s\"}", userProfile.getId(), authToken);
                return Response
                        .status(Response.Status.OK)
                        .entity(result)
                        .build();
            }
        }
        final String wrong = "{\"message\":\"Wrong login or password\"}";
        return Response
                .status(Response.Status.FORBIDDEN)
                .entity(wrong)
                .build();
    }

    @DELETE
    public Response logoutUser(@Context HttpServletRequest request,
                               @HeaderParam("auth_token") String currentToken) {
        final AccountService accountService = ctx.get(AccountService.class);
        accountService.logoutUser(currentToken);
        return Response
                .status(Response.Status.OK)
                .build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response checkAuth(@Context HttpServletRequest request,
                              @HeaderParam("auth_token") String currentToken) {
        final AccountService accountService = ctx.get(AccountService.class);
        UserProfile userProfile = null;
        if (currentToken != null) {
            userProfile = accountService.getUserBySessionID(currentToken);
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
