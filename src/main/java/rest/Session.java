package rest;

import accountService.AccountServiceImpl;
import dbStuff.dataSets.*;

import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.Date;


/**
 * MOSch-team test server for "Kill The Birds" game
 */
@Singleton
@Path("/session")
public class Session {
    private final AccountServiceImpl accountService;

    public Session(AccountServiceImpl accountService) {
        this.accountService = accountService;
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response loginUser(UserProfile user, @Context HttpServletRequest request)
            throws IOException {
        //final String sessionId = request.getSession().getId();
        final UserDataSet validUserDS = accountService.getUserDS(user.getLogin());
        final String payload;
        if (validUserDS != null) {
            if (user.getPassword().equals(validUserDS.getPassword())) {
                final String auth_token = AccountServiceImpl.getMD5(new Date() + validUserDS.getPassword());
                final long validID = validUserDS.getId();
                accountService.addActiveUser(validUserDS, auth_token);
                payload = String.format("{\"id\":\"%d\", \"auth_token\":\"%s\"}",
                        validID,
                        auth_token);
                return Response.status(Response.Status.OK).entity(payload).build();

                //payload = "{\"message\":\"Already logged in\"}";
                //return Response.status(Response.Status.FORBIDDEN).entity(payload).build();
            }
        }
        payload = "{\"message\":\"Wrong login or password\"}";
        return Response.status(Response.Status.FORBIDDEN).entity(payload).build();
    }

    @DELETE
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response logoutUser(@Context HttpServletRequest request,
                               @QueryParam("auth_token") String currentToken) {
        //final String current_token = request.getParameter("auth_token");
        accountService.removeActiveUser(currentToken);
        return Response.status(Response.Status.OK).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response checkAuth(@Context HttpServletRequest request,
                              @QueryParam("auth_token") String currentToken) {
        final AuthDataSet currentAuth = accountService.getActiveUser(currentToken);
        if (currentAuth == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        final String payload = String.format("{\"id\":\"%d\"}", currentAuth.getId());
        return Response.status(Response.Status.OK).entity(payload).build();
    }
}
