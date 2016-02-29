package rest;

import main.AccountService;

import javax.inject.Singleton;
import javax.json.JsonObject;
import javax.ws.rs.*;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
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
    public Response getAllUsers() {
        final Collection<UserProfile> allUsers = accountService.getAllUsers();

        return Response.status(Response.Status.OK).entity(allUsers.toArray(new UserProfile[allUsers.size()])).build();
    }

    @GET
    @Path("{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserByName(@PathParam("name") String name) {
        final UserProfile user = accountService.getUser(name);
        if(user == null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }else {
            return Response.status(Response.Status.OK).entity(user).build();
        }
    }


    @GET
    @Path("{id: [0-9]+}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserById(@PathParam("id") Long id) {
        final UserProfile user = accountService.getUser(id);
        if(user == null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }else {
            return Response.status(Response.Status.OK).entity(user).build();
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createUser(UserProfile user, @Context HttpHeaders headers){
        if(accountService.addUser(user)){
            String payload = String.format("{\"id\":\"%d\"}", user.getId());
            return Response.status(Response.Status.OK).entity(payload).build();
        } else {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
    }

    @POST
    @Path("{id :[0-9]+}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response editUser(@PathParam("id") Long id, UserProfile new_user, @Context HttpHeaders headers) {
        UserProfile user = accountService.getUser(id);
        if(user == null) {
            String payload = "{\"status\":\"403\",\"message\":\"Чужой юзер\"}";
            return Response.status(Response.Status.FORBIDDEN).entity(payload).build();
        }else {
            user.setLogin(new_user.getLogin());
            user.setPassword(new_user.getPassword());
            String payload = String.format("{\"id\":\"%d\"}", user.getId());
            return Response.status(Response.Status.OK).entity(payload).build();
        }


    }

}
