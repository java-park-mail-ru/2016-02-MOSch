package rest;

import main.AccountServiceImpl;
import java.io.*;
import javax.inject.Singleton;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


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
        throws IOException{
        final String sessionId = request.getSession().getId();
        final UserProfile validUser = accountService.getUser(user.getLogin());
        final String payload;
        if (validUser != null) {
            if (user.getPassword().equals(validUser.getPassword())) {
                if (accountService.addActiveUser(validUser, sessionId)) {
                    final long validID = validUser.getId();
                    payload = String.format("{\"id\":\"%d\", \"auth_token\":\"%s\"}",
                            validID,
                            AccountServiceImpl.getMD5(validUser.getId().toString()));
                    return Response.status(Response.Status.OK).entity(payload).build();
                }
                payload = "{\"message\":\"Already logged in\"}";
                return Response.status(Response.Status.FORBIDDEN).entity(payload).build();
            }
        }
        payload = "{\"message\":\"Wrong login or password\"}";
        return Response.status(Response.Status.FORBIDDEN).entity(payload).build();
    }


    @DELETE
    public Response logoutUser(@Context HttpServletRequest request){
        accountService.removeActiveUser(request.getSession().getId());
        return Response.status(Response.Status.OK).build();
    }


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response checkAuth(@Context HttpServletRequest request){
        final UserProfile currentUser = accountService.getActiveUser(request.getSession().getId());
        if (currentUser == null){
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        final String payload = String.format("{\"id\":\"%d\"}", currentUser.getId());
        return Response.status(Response.Status.OK).entity(payload).build();
    }

}
