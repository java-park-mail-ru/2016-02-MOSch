package rest;

import com.google.gson.Gson;
import base.AccountService;
import supportclasses.LoginScoreSet;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Objects;

/**
 * MOSch-team test server for "Build The Tower" game
 */
@SuppressWarnings("unused")
@Singleton
@Path("/user")
public class Users {
    @Inject
    private main.Context ctx;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllUsers() {
        final AccountService accountService = ctx.get(AccountService.class);

        final List<LoginScoreSet> allUsers = accountService.getTopUsers();
        final Gson gson = new Gson();
        final String entity = gson.toJson(allUsers);
        return Response
                .status(Response.Status.OK)
                .entity(entity)
                .build();
    }

    @GET
    @Path("{id: [0-9]+}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserById(@Context HttpServletRequest request,
                                @PathParam("id") Long id) {
        final AccountService accountService = ctx.get(AccountService.class);

        final HttpSession session = request.getSession(false);
        String sessionID = null;
        if (session != null) {
            sessionID = session.getId();
        }
        if (sessionID == null) {
            return Response
                    .status(Response.Status.UNAUTHORIZED)
                    .build();
        }
        final UserProfile user = accountService.getUserBySessionID(sessionID);
        if (user != null) {
            if (Objects.equals(user.getId(), id)) {
                final UserProfile lookFor = accountService.getUserByID(id);
                if (lookFor != null) {
                    return Response
                            .status(Response.Status.OK)
                            .entity(lookFor)
                            .build();
                } else {
                    final String notFound = "{\"message\":\"User not found\"}";
                    return Response
                            .status(Response.Status.NOT_FOUND)
                            .entity(notFound)
                            .build();
                }
            }
            final String accessDenied = "{\"message\":\"Access denied\"}";
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

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createUser(UserProfile user) {
        final AccountService accountService = ctx.get(AccountService.class);

        if (user == null) {
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .build();
        }
        final Long userID = accountService.addUser(user);
        if (userID != null) {
            final String payload = String.format("{\"id\":\"%d\"}", userID);
            return Response
                    .status(Response.Status.OK)
                    .entity(payload)
                    .build();
        } else {
            final String wrong = "{\"message\":\"Incorrect login or password\"}";
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity(wrong)
                    .build();
        }
    }

    @PUT
    @Path("{id :[0-9]+}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response editUser(@Context HttpServletRequest request,
                             @PathParam("id") Long id, UserProfile newUser) {
        final AccountService accountService = ctx.get(AccountService.class);

        final HttpSession session = request.getSession(false);
        String sessionID = null;
        if (session != null) {
            sessionID = session.getId();
        }
        if (sessionID == null) {
            return Response
                    .status(Response.Status.UNAUTHORIZED)
                    .build();
        }
        final UserProfile currentUser = accountService.getUserBySessionID(sessionID);
        if (currentUser == null) {
            return Response
                    .status(Response.Status.UNAUTHORIZED)
                    .build();
        }
        if (Objects.equals(currentUser.getId(), id)) {
            final UserProfile oldUser = accountService.getUserByID(id);
            if (oldUser != null && accountService.updateUser(id, newUser)) {
                final String payload = String.format("{\"id\":\"%d\"}", id);
                return Response
                        .status(Response.Status.OK)
                        .entity(payload)
                        .build();
            } else {
                final String notFound = "{\"message\":\"User not found\"}";
                return Response
                        .status(Response.Status.NOT_FOUND)
                        .entity(notFound)
                        .build();
            }
        } else {
            final String accessDenied = "{\"message\":\"Access denied\"}";
            return Response
                    .status(Response.Status.FORBIDDEN)
                    .entity(accessDenied)
                    .build();
        }
    }

    @DELETE
    @Path("{id :[0-9]+}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteUser(@Context HttpServletRequest request,
                               @PathParam("id") Long id) {
        final AccountService accountService = ctx.get(AccountService.class);

        final HttpSession session = request.getSession(false);
        String sessionID = null;
        if (session != null) {
            sessionID = session.getId();
        }
        if (sessionID == null) {
            return Response
                    .status(Response.Status.UNAUTHORIZED)
                    .build();
        }
        final UserProfile currentUser = accountService.getUserBySessionID(sessionID);
        if (currentUser == null) {
            return Response
                    .status(Response.Status.UNAUTHORIZED)
                    .build();
        }
        if (Objects.equals(currentUser.getId(), id)) {
            final UserProfile oldUser = accountService.getUserByID(id);
            if (oldUser != null) {
                accountService.removeUser(id);
                final String success = "{\"message\":\"Successfully removed\"}";
                return Response
                        .status(Response.Status.OK)
                        .entity(success)
                        .build();
            } else {
                final String notFound = "{\"message\":\"User not found\"}";
                return Response
                        .status(Response.Status.NOT_FOUND)
                        .entity(notFound)
                        .build();
            }
        } else {
            final String accessDenied = "{\"message\":\"Access denied\"}";
            return Response
                    .status(Response.Status.FORBIDDEN)
                    .entity(accessDenied)
                    .build();
        }
    }
}
