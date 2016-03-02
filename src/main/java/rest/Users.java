package rest;

//import com.sun.tools.internal.xjc.reader.xmlschema.bindinfo.BIConversion;
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
@Path("/user")
public class Users {
    private AccountService accountService;

    public Users(AccountService accountService) {
        this.accountService = accountService;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllUsers(@Context HttpServletRequest request) {
        String sessionId = request.getSession().getId();
        UserProfile currentUser = accountService.getActiveUser(sessionId);
        String payload = "{\"status\":\"403\",\"message\":\"Access denied\"}";

        if (currentUser != null) {
            if (currentUser.getRole() == UserProfile.roleEnum.admin) {
                final Collection<UserProfile> allUsers = accountService.getAllUsers();
                return Response.status(Response.Status.OK).entity(allUsers.toArray(new UserProfile[allUsers.size()])).build();
            }
        }
        return Response.status(Response.Status.FORBIDDEN).entity(payload).build();

    }

    @GET
    @Path("{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserByName(@PathParam("name") String name, @Context HttpServletRequest request) {
        String sessionId = request.getSession().getId();
        UserProfile currentUser = accountService.getActiveUser(sessionId);
        String payload = "{\"status\":\"404\",\"message\":\"User not found\"}";
        final UserProfile user = accountService.getUser(name);

        if (currentUser != null) {
            if (currentUser == user || currentUser.getRole() == UserProfile.roleEnum.admin) {
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
        String sessionId = request.getSession().getId();
        UserProfile currentUser = accountService.getActiveUser(sessionId);
        String payload = "{\"status\":\"404\",\"message\":\"User not found\"}";

        if (currentUser != null) {
            if (currentUser == user || currentUser.getRole() == UserProfile.roleEnum.admin) {
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
    public Response createUser(UserProfile user, @Context HttpServletRequest request){
        String sessionId = request.getSession().getId();
        UserProfile currentUser = accountService.getActiveUser(sessionId);
        String payload;
        if (currentUser != null) {
            if (currentUser.getRole() == UserProfile.roleEnum.admin) {
                if (accountService.addUser(user)) {
                    payload = String.format("{\"id\":\"%d\"}", user.getId());
                    return Response.status(Response.Status.OK).entity(payload).build();
                }
            }
        }
        payload = "{\"status\":\"403\",\"message\":\"Access denied\"}";
        return Response.status(Response.Status.FORBIDDEN).entity(payload).build();
    }

    @POST
    @Path("{id :[0-9]+}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response editUser(@PathParam("id") Long id, UserProfile new_user, @Context HttpServletRequest request) {
        String sessionId = request.getSession().getId();
        UserProfile currentUser = accountService.getActiveUser(sessionId);
        UserProfile user = accountService.getUser(id);
        String payload;

        if (currentUser != user && currentUser.getRole() != UserProfile.roleEnum.admin){
            payload = "{\"status\":\"403\",\"message\":\"Access denied\"}";
            return Response.status(Response.Status.FORBIDDEN).entity(payload).build();
        }else if(user == null) {
            payload = "{\"status\":\"404\",\"message\":\"User not found\"}";
            return Response.status(Response.Status.NOT_FOUND).entity(payload).build();
        }else {
            accountService.removeActiveUser(id); //logout by id if edited user is online
            user.setLogin(new_user.getLogin());
            user.setPassword(new_user.getPassword());
            payload = String.format("{\"id\":\"%d\"}", user.getId());
            return Response.status(Response.Status.OK).entity(payload).build();
        }
    }

    @DELETE
    @Path("{id :[0-9]+}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteUser(@PathParam("id") Long id, @Context HttpServletRequest request ){
        String sessionId = request.getSession().getId();
        UserProfile currentUser = accountService.getActiveUser(request.getSession().getId());
        UserProfile user = accountService.getUser(id);
        String payload = "{\"status\":\"403\",\"message\":\"Access denied\"}";
        if (currentUser != null) {
            if (currentUser != user && currentUser.getRole() != UserProfile.roleEnum.admin) {
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
    private Boolean is_active(String sessionId, UserProfile user) {
        if (accountService.getActiveUser(sessionId) == user)
            return true;
        return false;

    }
}
