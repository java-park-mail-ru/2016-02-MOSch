package rest;

//import com.sun.tools.internal.xjc.reader.xmlschema.bindinfo.BIConversion;

import dbStuff.AccountService;
import dbStuff.dataSets.AuthDataSet;
import dbStuff.dataSets.UserDataSet;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collection;
import java.util.Objects;

/**
 * MOSch-team test server for "Kill The Birds" game
 */
@Singleton
@Path("/user")
public class Users {
    @Inject
    private main.Context ctx;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllUsers(@Context HttpServletRequest request,
                                @HeaderParam("auth_token") String currentToken) {
        final AccountService accountService = ctx.get(AccountService.class);
        final Collection allUsers = accountService.getAllUsers();
        return Response
                .status(Response.Status.OK)
                .entity(allUsers.toArray(new UserProfile[allUsers.size()]))
                .build();
    }

    @GET
    @Path("{id: [0-9]+}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserById(@PathParam("id") Long id,
                                @HeaderParam("auth_token") String currentToken) {
        final AccountService accountService = ctx.get(AccountService.class);
        final UserDataSet user = accountService.getUser(id);
        final AuthDataSet currentAuth = accountService.getActiveUser(currentToken);

        if (currentAuth != null) {
            String payload = "{\"status\":\"404\",\"message\":\"User not found\"}";
            if (Objects.equals(currentAuth.getUser(), user) || currentAuth.getUser().isAdmin()) {
                if (user == null) {
                    return Response
                            .status(Response.Status.NOT_FOUND)
                            .entity(payload)
                            .build();
                } else {
                    return Response
                            .status(Response.Status.OK)
                            .entity(user)
                            .build();
                }
            }
            payload = "{\"status\":\"403\",\"message\":\"Access denied\"}";
            return Response
                    .status(Response.Status.FORBIDDEN)
                    .entity(payload)
                    .build();

        } else {
            return Response
                    .status(Response.Status.UNAUTHORIZED)
                    .build();
        }
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createUser(UserProfile user) {
        final AccountService accountService = ctx.get(AccountService.class);
        if (accountService.addUser(user)) {
            UserDataSet userDS = accountService.getUserDS(user.getLogin());
            final String payload = String.format("{\"id\":\"%d\"}", userDS.getId());
            return Response
                    .status(Response.Status.OK)
                    .entity(payload)
                    .build();
        }
        return Response
                .status(Response.Status.BAD_REQUEST)
                .build();
    }

    @POST
    @Path("{id :[0-9]+}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response editUser(@PathParam("id") Long id, UserProfile newUser,
                             @HeaderParam("auth_token") String currentToken) {
        final AccountService accountService = ctx.get(AccountService.class);
        final AuthDataSet currentAuth = accountService.getActiveUser(currentToken);
        final UserDataSet user = accountService.getUser(id);
        final String payload;

        if (!Objects.equals(currentAuth.getUser(), user) && currentAuth.getUser().isAdmin()) {
            payload = "{\"status\":\"403\",\"message\":\"Access denied\"}";
            return Response
                    .status(Response.Status.FORBIDDEN)
                    .entity(payload)
                    .build();
        } else if (user == null) {
            payload = "{\"status\":\"404\",\"message\":\"User not found\"}";
            return Response
                    .status(Response.Status.NOT_FOUND)
                    .entity(payload)
                    .build();
        } else {
            user.setLogin(newUser.getLogin());
            user.setPassword(newUser.getPassword());
            accountService.updateUser(user);
            payload = String.format("{\"id\":\"%d\"}", user.getId());
            return Response
                    .status(Response.Status.OK)
                    .entity(payload)
                    .build();
        }
    }

    @DELETE
    @Path("{id :[0-9]+}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteUser(@PathParam("id") Long id,
                               @HeaderParam("auth_token") String currentToken) {

        final AccountService accountService = ctx.get(AccountService.class);
        final AuthDataSet currentAuth = accountService.getActiveUser(currentToken);
        final UserDataSet user = accountService.getUser(id);
        String payload = "{\"status\":\"403\",\"message\":\"Access denied\"}";
        if (currentAuth != null) {
            if (!Objects.equals(currentAuth.getUser(), user) && currentAuth.getUser().isAdmin()) {
                payload = "{\"status\":\"403\",\"message\":\"Access denied\"}";
                return Response
                        .status(Response.Status.FORBIDDEN)
                        .entity(payload)
                        .build();
            } else if (user == null) {
                payload = "{\"status\":\"404\",\"message\":\"User not found\"}";
                return Response
                        .status(Response.Status.NOT_FOUND)
                        .entity(payload)
                        .build();
            } else {
                accountService.removeUser(user);
                return Response
                        .status(Response.Status.OK)
                        .build();
            }
        }
        return Response
                .status(Response.Status.FORBIDDEN)
                .entity(payload)
                .build();
    }

    @NotNull
    private Boolean isActive(String sessionId, UserProfile user) {
        final AccountService accountService = ctx.get(AccountService.class);
        return Objects.equals(accountService.getActiveUser(sessionId), user);
    }
}
