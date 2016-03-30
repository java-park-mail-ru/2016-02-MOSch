package main;

import accountService.AccountServiceImpl;
import rest.Session;
import rest.Users;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

/**
 * MOSch-team test server for "Kill The Birds" game
 */
@ApplicationPath("api")
public class RestApplication extends Application {
    @Override
    public Set<Object> getSingletons() {
        final HashSet<Object> objects = new HashSet<>();
        final AccountServiceImpl bd = new AccountServiceImpl();
        objects.add(new Users(bd));
        objects.add(new Session(bd));
        return objects;
    }
}
