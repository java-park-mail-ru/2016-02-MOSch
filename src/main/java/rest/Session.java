package rest;

import main.AccountService;
import org.jetbrains.annotations.NotNull;

import javax.inject.Singleton;
import javax.json.JsonObject;
import javax.print.attribute.standard.Media;
import javax.ws.rs.*;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collection;

/**
 * MOSch-team test server for "Kill The Birds" game
 */
@Singleton
@Path("/session")
public class Session {
    private AccountService accountService;

    public Session(AccountService accountService) {
        this.accountService = accountService;
    }


    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response loginUser(UserProfile user, @Context HttpServletRequest request){
        final String sessionId = request.getSession().getId();
        final UserProfile validUser = accountService.getUser(user.getLogin());
        String payload;
        if (validUser != null) {
            if (user.getPassword().equals(validUser.getPassword())) {
                if (accountService.addActiveUser(validUser, sessionId)) {
                    payload = String.format("{\"id\":\"%d\"}", validUser.getId());
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
        String payload = String.format("{\"id\":\"%d\"}", currentUser.getId());
        return Response.status(Response.Status.OK).entity(payload).build();
    }

}
