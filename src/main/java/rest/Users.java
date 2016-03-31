package rest;

import main.AccountService;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Objects;

/**
 * MOSch-team test server for "Kill The Birds" game
 */
@SuppressWarnings("unused")
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
        final List<UserProfile> allUsers = accountService.getAllUsers();
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
        final UserProfile user = accountService.getUserBySessionID(currentToken);
        if (user != null) {
            if (user.getIsAdmin() || Objects.equals(user.getId(), id)) {
                final UserProfile lookFor = accountService.getUserByID(id);
                if (lookFor != null) {
                    return Response
                            .status(Response.Status.OK)
                            .entity(lookFor)
                            .build();
                } else {
                    final String notFound = "{\"status\":\"404\",\"message\":\"User not found\"}";
                    return Response
                            .status(Response.Status.NOT_FOUND)
                            .entity(notFound)
                            .build();
                }
            }
            final String accessDenied = "{\"status\":\"403\",\"message\":\"Access denied\"}";
            return Response
                    .status(Response.Status.FORBIDDEN)
                    .entity(accessDenied)
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
        final Long userID = accountService.addUser(user);
        if (userID != null) {
            final String payload = String.format("{\"id\":\"%d\"}", userID);
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
        final UserProfile currentUser = accountService.getUserBySessionID(currentToken);
        if (currentUser == null) {
            return Response
                    .status(Response.Status.UNAUTHORIZED)
                    .build();
        }
        if (Objects.equals(currentUser.getId(), id) || currentUser.getIsAdmin()) {
            final UserProfile oldUser = accountService.getUserByID(id);
            if (oldUser != null) {
                accountService.updateUser(id, newUser);
                final String payload = String.format("{\"id\":\"%d\"}", id);
                return Response
                        .status(Response.Status.OK)
                        .entity(payload)
                        .build();
            } else {
                final String notFound = "{\"status\":\"404\",\"message\":\"User not found\"}";
                return Response
                        .status(Response.Status.NOT_FOUND)
                        .entity(notFound)
                        .build();
            }
        } else {
            final String accessDenied = "{\"status\":\"403\",\"message\":\"Access denied\"}";
            return Response
                    .status(Response.Status.FORBIDDEN)
                    .entity(accessDenied)
                    .build();
        }
    }

    @DELETE
    @Path("{id :[0-9]+}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteUser(@PathParam("id") Long id,
                               @HeaderParam("auth_token") String currentToken) {
        final AccountService accountService = ctx.get(AccountService.class);
        final UserProfile currentUser = accountService.getUserBySessionID(currentToken);
        if (currentUser == null) {
            return Response
                    .status(Response.Status.UNAUTHORIZED)
                    .build();
        }
        if (Objects.equals(currentUser.getId(), id) || currentUser.getIsAdmin()) {
            final UserProfile oldUser = accountService.getUserByID(id);
            if (oldUser != null) {
                accountService.removeUser(id);
                final String success = "{\"status\":\"200\",\"message\":\"Successfully removed\"}";
                return Response
                        .status(Response.Status.OK)
                        .entity(success)
                        .build();
            } else {
                final String notFound = "{\"status\":\"404\",\"message\":\"User not found\"}";
                return Response
                        .status(Response.Status.NOT_FOUND)
                        .entity(notFound)
                        .build();
            }
        } else {
            final String accessDenied = "{\"status\":\"403\",\"message\":\"Access denied\"}";
            return Response
                    .status(Response.Status.FORBIDDEN)
                    .entity(accessDenied)
                    .build();
        }
    }
}
