package rest;

//import com.sun.tools.internal.xjc.reader.xmlschema.bindinfo.BIConversion;
import main.AccountService;
import org.jetbrains.annotations.NotNull;

import javax.inject.Singleton;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collection;
import java.util.Objects;

/**
 * MOSch-team test server for "Kill The Birds" game
 */
@SuppressWarnings("unused")
@Singleton
@Path("/user")
public class Users {
    private final AccountService accountService;

    public Users(AccountService accountService) {
        this.accountService = accountService;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllUsers(@Context HttpServletRequest request) {
        final String sessionId = request.getSession().getId();
        final UserProfile currentUser = accountService.getActiveUser(sessionId);

        if (currentUser != null) {
            if (currentUser.getRole() == UserProfile.RoleEnum.ADMIN) {
                final Collection<UserProfile> allUsers = accountService.getAllUsers();
                return Response.status(Response.Status.OK).entity(allUsers.toArray(new UserProfile[allUsers.size()])).build();
            }
        }
        final String payload = "{\"status\":\"403\",\"message\":\"Access denied\"}";
        return Response.status(Response.Status.FORBIDDEN).entity(payload).build();

    }

    @GET
    @Path("{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserByName(@PathParam("name") String name, @Context HttpServletRequest request) {
        final String sessionId = request.getSession().getId();
        final UserProfile currentUser = accountService.getActiveUser(sessionId);
        final UserProfile user = accountService.getUser(name);

        if (currentUser != null) {
            String payload = "{\"status\":\"404\",\"message\":\"User not found\"}";
            if (Objects.equals(currentUser, user) || currentUser.getRole() == UserProfile.RoleEnum.ADMIN) {
                if(user == null){return Response.status(Response.Status.NOT_FOUND).entity(payload).build();}
                else return Response.status(Response.Status.OK).entity(user).build();
            }
            payload = "{\"status\":\"403\",\"message\":\"Access denied\"}";
            return Response.status(Response.Status.FORBIDDEN).entity(payload).build();

        }else {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
    }


    @GET
    @Path("{id: [0-9]+}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserById(@PathParam("id") Long id, @Context HttpServletRequest request) {
        final UserProfile user = accountService.getUser(id);
        final String sessionId = request.getSession().getId();
        final UserProfile currentUser = accountService.getActiveUser(sessionId);

        if (currentUser != null) {
            String payload = "{\"status\":\"404\",\"message\":\"User not found\"}";
            if (Objects.equals(currentUser, user) || currentUser.getRole() == UserProfile.RoleEnum.ADMIN) {
                if(user == null){return Response.status(Response.Status.NOT_FOUND).entity(payload).build();}
                else return Response.status(Response.Status.OK).entity(user).build();
            }
            payload = "{\"status\":\"403\",\"message\":\"Access denied\"}";
            return Response.status(Response.Status.FORBIDDEN).entity(payload).build();

        }else {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createUser(UserProfile user){
        if (accountService.addUser(user)) {
            final String payload = String.format("{\"id\":\"%d\"}", user.getId());
            return Response.status(Response.Status.OK).entity(payload).build();
        }
        return Response.status(Response.Status.BAD_REQUEST).build();
    }

    @POST
    @Path("{id :[0-9]+}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response editUser(@PathParam("id") Long id, UserProfile newUser, @Context HttpServletRequest request) {
        final String sessionId = request.getSession().getId();
        final UserProfile currentUser = accountService.getActiveUser(sessionId);
        final UserProfile user = accountService.getUser(id);
        final String payload;

        if (!Objects.equals(currentUser, user) && currentUser.getRole() != UserProfile.RoleEnum.ADMIN){
            payload = "{\"status\":\"403\",\"message\":\"Access denied\"}";
            return Response.status(Response.Status.FORBIDDEN).entity(payload).build();
        }else if(user == null) {
            payload = "{\"status\":\"404\",\"message\":\"User not found\"}";
            return Response.status(Response.Status.NOT_FOUND).entity(payload).build();
        }else {
            accountService.removeActiveUser(id); //logout by id if edited user is online
            user.setLogin(newUser.getLogin());
            user.setPassword(newUser.getPassword());
            payload = String.format("{\"id\":\"%d\"}", user.getId());
            return Response.status(Response.Status.OK).entity(payload).build();
        }
    }

    @DELETE
    @Path("{id :[0-9]+}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteUser(@PathParam("id") Long id, @Context HttpServletRequest request ){
        final String sessionId = request.getSession().getId();
        final UserProfile currentUser = accountService.getActiveUser(sessionId);
        final UserProfile user = accountService.getUser(id);
        String payload = "{\"status\":\"403\",\"message\":\"Access denied\"}";
        if (currentUser != null) {
            if (!Objects.equals(currentUser, user) && currentUser.getRole() != UserProfile.RoleEnum.ADMIN) {
                payload = "{\"status\":\"403\",\"message\":\"Access denied\"}";
                return Response.status(Response.Status.FORBIDDEN).entity(payload).build();
            } else if (user == null) {
                payload = "{\"status\":\"404\",\"message\":\"User not found\"}";
                return Response.status(Response.Status.NOT_FOUND).entity(payload).build();
            } else {
                accountService.getAllUsers().remove(user);
                accountService.removeActiveUser(id); //logout by id if edited user is online
                return Response.status(Response.Status.OK).build();
            }
        }
        return Response.status(Response.Status.FORBIDDEN).entity(payload).build();
    }

    @NotNull
    private Boolean isActive(String sessionId, UserProfile user) {
        return Objects.equals(accountService.getActiveUser(sessionId), user);

    }
}
