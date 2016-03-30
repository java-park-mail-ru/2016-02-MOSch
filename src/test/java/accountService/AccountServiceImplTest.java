package accountService;

import dbStuff.dataSets.UserDataSet;
import org.eclipse.persistence.internal.oxm.schema.model.List;
import org.hibernate.mapping.Collection;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import rest.UserProfile;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

/**
 * Created by KOPTE3 on 30.03.2016.
 */
public class AccountServiceImplTest {
    AccountServiceImpl accountService;

    @Before
    public void setUp() throws Exception {
        accountService = new AccountServiceImpl();
    }

    @Test
    public void testGetUserDS() throws Exception {
        assertEquals(0, (long) accountService.countUsers());
        UserProfile profile = new UserProfile("mytest", "password");
        accountService.addUser(profile);
        UserDataSet dataSet = accountService.getUserDS(profile.getLogin());
        assertEquals(profile.getLogin(), dataSet.getLogin());
    }

    @Test
    public void testAddCountUsers() throws Exception {
        assertEquals(0, (long) accountService.countUsers());
        UserProfile profile = new UserProfile("mytest", "password");
        accountService.addUser(profile);
        assertEquals(1, (long) accountService.countUsers());
        for (int i = 0; i < 19; i++) {
            profile.setLogin("testlogin" + ((Integer) i).toString());
            accountService.addUser(profile);
        }
        assertEquals(20, (long) accountService.countUsers());
    }

    @Test
    public void test_AddGet_Users() throws Exception {
        UserProfile profile = new UserProfile("usertest", "123456");
        accountService.addUser(profile);
        UserDataSet userDS = accountService.getUser(1L);
        assertEquals("usertest",userDS.getLogin());
        assertEquals("123456",userDS.getPassword());

        userDS = accountService.getUserDS("usertest");
        assertEquals(1L,userDS.getId());
        assertEquals("123456",userDS.getPassword());

        assertEquals(1, (long) accountService.countUsers());
    }

    @Test
    public void testGetAllUsers() throws Exception {
        ArrayList<UserProfile> list = new ArrayList<>();
        UserProfile profile = new UserProfile("usertest", "123456");
        for(int i = 0; i < 10; i++) {
            profile.setLogin("usertest" + ((Integer) i).toString());
            accountService.addUser(profile);
            list.add(profile);
        }
        assertEquals(list.size(),accountService.getAllUsers().size());
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

    @After
    public void tearDown() throws Exception {
        accountService.sessionFactory.close();
    }
}
