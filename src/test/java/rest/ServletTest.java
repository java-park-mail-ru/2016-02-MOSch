package rest;


import com.google.gson.Gson;
import main.AccountService;
import main.AccountServiceImpl;
import main.Context;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by KOPTE3 on 30.03.2016.
 */
@SuppressWarnings("unused")
public class ServletTest extends JerseyTest {
    @Override
    protected Application configure() {
        final Context context = new Context();
        context.put(AccountService.class, new AccountServiceImpl("test_hibernate.cfg.xml"));

        final ResourceConfig config = new ResourceConfig(Users.class, Session.class);
        final HttpServletRequest request = mock(HttpServletRequest.class);
        final HttpSession session = mock(HttpSession.class);
        //noinspection AnonymousInnerClassMayBeStatic
        config.register(new AbstractBinder() {
            @Override
            protected void configure() {
                bind(context);
                bind(request).to(HttpServletRequest.class);
                bind(session).to(HttpSession.class);
                when(request.getSession()).thenReturn(session);
                when(session.getId()).thenReturn("session");
            }
        });

        return config;
    }

    @Test
    public void testSignInUser() throws com.google.gson.JsonSyntaxException {
        final Gson gson = new Gson();
        final UserProfile user1 = new UserProfile("test1", "testpass1");
        final UserProfile user2 = new UserProfile("test2", "testpass2");
        final RegisterInfo accepted1 = new RegisterInfo(1L);
        final RegisterInfo accepted2 = new RegisterInfo(2L);
        final RegisterInfo result1 = gson.fromJson(target("user").request(MediaType.APPLICATION_JSON).post(Entity.json(user1), String.class), RegisterInfo.class);
        final RegisterInfo result2 = gson.fromJson(target("user").request(MediaType.APPLICATION_JSON).post(Entity.json(user2), String.class), RegisterInfo.class);
        assertEquals(accepted1.id, result1.id);
        assertEquals(accepted2.id, result2.id);

        Response response = target("user").request(MediaType.APPLICATION_JSON).post(Entity.json(new UserProfile()));
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        response = target("user").request(MediaType.APPLICATION_JSON).post(Entity.json(new UserProfile("", "pass")));
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        response = target("user").request(MediaType.APPLICATION_JSON).post(Entity.json(new UserProfile("login", "pass")));
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        response = target("user").request(MediaType.APPLICATION_JSON).post(Entity.json(new UserProfile("", "password")));
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        response = target("user").request(MediaType.APPLICATION_JSON).post(Entity.json(new UserProfile(" ", "pass")));
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        response = target("user").request(MediaType.APPLICATION_JSON).post(Entity.json(new UserProfile(" login", "password1")));
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        response = target("user").request(MediaType.APPLICATION_JSON).post(Entity.json(new UserProfile("login", "password1 ")));
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        response = target("user").request(MediaType.APPLICATION_JSON).post(Entity.json(new UserProfile("login", "pa 1222 121")));
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        response = target("user").request(MediaType.APPLICATION_JSON).post(Entity.json(new UserProfile("-dcddd", "....ddww")));
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    private static final class RegisterInfo {
        @NotNull
        private final Long id;

        private RegisterInfo(@NotNull Long id) {
            this.id = id;
        }
    }
}
