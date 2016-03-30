package accountService;

import dbStuff.dataSets.UserDataSet;
import org.junit.Ignore;
import org.junit.Test;
import rest.UserProfile;

import static org.junit.Assert.assertEquals;

/**
 * Created by KOPTE3 on 30.03.2016.
 */
public class AccountServiceImplTest {

    @Test
    @Ignore
    public void testAddCountUsers() throws Exception {
        AccountServiceImpl service = new AccountServiceImpl();
        assertEquals(0, service.countUsers());
        UserProfile profile = new UserProfile("mytest", "password");
        service.addUser(profile);
        assertEquals(1, service.countUsers());
        for (int i = 0; i < 119; i++) {
            profile.setLogin("testlogin" + ((Integer) i).toString());
            service.addUser(profile);
        }
        assertEquals(120, service.countUsers());
    }

    @Test
    @Ignore
    public void testGetUserDS() throws Exception {
        AccountServiceImpl service = new AccountServiceImpl();
        assertEquals(0, service.countUsers());
        UserProfile profile = new UserProfile("mytest", "password");
        service.addUser(profile);
        UserDataSet dataSet = service.getUserDS(profile.getLogin());
        assertEquals(profile.getLogin(), dataSet.getLogin());
    }

    @Test
    public void testGetMD5() throws Exception {
        final int len1 = AccountServiceImpl.getMD5("").length();
        assertEquals(32, len1);
        final int len2 = AccountServiceImpl.getMD5("mystring").length();
        assertEquals(32, len2);
    }

    @Test(expected = NullPointerException.class)
    public void testGetMD5ofNull() throws Exception {
        AccountServiceImpl.getMD5(null);
    }
}