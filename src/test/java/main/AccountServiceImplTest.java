package main;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import rest.UserProfile;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by KOPTE3 on 30.03.2016.
 */
@SuppressWarnings({"MagicNumber", "unused"})
public class AccountServiceImplTest {
    private final UserProfile user1 = new UserProfile("login1", "password1");
    private final UserProfile user2 = new UserProfile("login2", "password1");
    private final UserProfile user3 = new UserProfile("login3", "password1");
    private final UserProfile user4 = new UserProfile("login4", "password1");
    private final UserProfile user5 = new UserProfile("login5", "password1");
    private final UserProfile user6 = new UserProfile("login5", "special");
    AccountServiceImpl accountService;

    @Before
    public void setUp() {
        accountService = new AccountServiceImpl("test_hibernate.cfg.xml");
        user1.setRole(UserProfile.RoleEnum.ADMIN);
    }

    @Test
    public void testAddUser() throws Exception {
        final Long id1 = accountService.addUser(user1);
        final Long id2 = accountService.addUser(user2);
        final Long id3 = accountService.addUser(user3);
        final Long id4 = accountService.addUser(user4);
        final Long id5 = accountService.addUser(user5);

        assertNotNull(id1);
        assertNotNull(id2);
        assertNotNull(id3);
        assertNotNull(id4);
        assertNotNull(id5);
        final Long id6 = accountService.addUser(user6);

        assertNull(id6);
    }

    @Test
    public void testGetUserByID() throws Exception {
        final Long id1 = accountService.addUser(user1);
        final Long id2 = accountService.addUser(user2);

        assertNotNull(id1);
        assertNotNull(id2);
        final UserProfile userProfile1 = accountService.getUserByID(id1);
        final UserProfile userProfile2 = accountService.getUserByID(id2);
        final UserProfile profileWrong = accountService.getUserByID(666);

        assertNotNull(userProfile1);
        assertNotNull(userProfile2);
        assertNull(profileWrong);

        assertEquals("login1", userProfile1.getLogin());
        assertEquals("login2", userProfile2.getLogin());
        assertTrue(userProfile1.getIsAdmin());
    }

    @Test
    public void testGetUserByLogin() throws Exception {
        accountService.addUser(user1);
        accountService.addUser(user2);

        final UserProfile userProfile1 = accountService.getUserByLogin(user1.getLogin());
        final UserProfile userProfile2 = accountService.getUserByLogin(user2.getLogin());
        final UserProfile profileWrong = accountService.getUserByLogin("wrong");

        assertNotNull(userProfile1);
        assertNotNull(userProfile2);
        assertNull(profileWrong);

        assertEquals("login1", userProfile1.getLogin());
        assertEquals("login2", userProfile2.getLogin());
        assertTrue(userProfile1.getIsAdmin());
    }

    @Test
    public void testCountUsers() throws Exception {
        assertEquals(0, accountService.countUsers());
        accountService.addUser(user1);
        assertEquals(1, accountService.countUsers());
        accountService.addUser(user2);
        accountService.addUser(user3);
        accountService.addUser(user4);
        accountService.addUser(user5);
        assertEquals(5, accountService.countUsers());
    }

    @Test
    public void testRemoveUser() throws Exception {
        final Long id1 = accountService.addUser(user1);
        final Long id2 = accountService.addUser(user2);

        assertNotNull(id1);
        assertNotNull(id2);

        accountService.removeUser(id2);
        assertNull(accountService.getUserByID(id2));
        assertEquals(1, accountService.countUsers());
        accountService.removeUser(666);
        assertEquals(1, accountService.countUsers());
    }

    @Test
    @Ignore
    public void testUpdateUser() throws Exception {
        final Long id1 = accountService.addUser(user1);
        final Long id2 = accountService.addUser(user2);

        assertNotNull(id1);
        assertNotNull(id2);

        accountService.updateUser(id2, user3);
        accountService.updateUser(666, user3);
        final UserProfile newUser2 = accountService.getUserByID(id2);
        assertNotNull(newUser2);
        assertEquals("login3", newUser2.getLogin());
    }

    @Test
    public void testIsUserExists() throws Exception {
        accountService.addUser(user1);
        accountService.addUser(user2);
        accountService.addUser(user3);
        accountService.addUser(user4);
        accountService.addUser(user5);

        assertNotNull(accountService.getUserID(user2.getLogin(), user2.getPassword()));
        assertNull(accountService.getUserID(user2.getLogin(), "wrong"));
        assertNull(accountService.getUserID("wrong", "wrong"));
    }

    @Test
    @Ignore
    public void testGetAllUsers() {
        accountService.addUser(user1);
        accountService.addUser(user2);
        accountService.addUser(user3);
        accountService.addUser(user4);
        accountService.addUser(user5);

        final List<UserProfile> users = accountService.getAllUsers();
        assertNotNull(users);
        assertTrue(users.get(0).getLogin().equals("login1")
                && users.get(1).getLogin().equals("login2")
                && users.get(2).getLogin().equals("login3")
                && users.get(3).getLogin().equals("login4")
                && users.get(4).getLogin().equals("login5"));
    }

    @Test
    public void testLoginUser() throws Exception {
        accountService.addUser(user1);
        accountService.addUser(user2);
        accountService.addUser(user3);
        accountService.addUser(user4);
        accountService.addUser(user5);

        final String token1 = accountService.loginUser(user1.getLogin(), user1.getPassword());
        final String token2 = accountService.loginUser(user2.getLogin(), user2.getPassword());
        final String token5 = accountService.loginUser(user5.getLogin(), user5.getPassword());
        assertNotNull(token1);
        assertNotNull(token2);
        assertNotNull(token5);
        assertNotEquals(token1, token2);

        final String token1new = accountService.loginUser(user1.getLogin(), user1.getPassword());

        assertNotNull(token1new);
        assertNull(accountService.loginUser(user1.getLogin(), "wrong"));
        assertNull(accountService.loginUser("wrong", "wrong"));

        assertNotNull(accountService.loginUser(token2));
        assertNull(accountService.loginUser("wrong"));
        assertNotNull(accountService.loginUser(token1new));
    }

    @Test
    public void testLogoutUser() throws Exception {
        accountService.addUser(user1);
        accountService.addUser(user2);
        accountService.addUser(user3);
        accountService.addUser(user4);
        accountService.addUser(user5);

        final String token1 = accountService.loginUser(user1.getLogin(), user1.getPassword());
        final String token2 = accountService.loginUser(user2.getLogin(), user2.getPassword());
        final String token5 = accountService.loginUser(user5.getLogin(), user5.getPassword());
        assertNotNull(token1);
        assertNotNull(token2);
        assertNotNull(token5);

        accountService.logoutUser(token1);
        accountService.logoutUser(token2);
        accountService.logoutUser("wrong");

        assertNull(accountService.loginUser(token1));
        assertNull(accountService.loginUser(token2));
        assertNotNull(accountService.loginUser(token5));
    }

    @Test
    public void testIsLoggedIn() throws Exception {
        accountService.addUser(user1);
        accountService.addUser(user2);
        accountService.addUser(user3);
        accountService.addUser(user4);
        accountService.addUser(user5);

        final String token1 = accountService.loginUser(user1.getLogin(), user1.getPassword());
        final String token2 = accountService.loginUser(user2.getLogin(), user2.getPassword());
        final String token5 = accountService.loginUser(user5.getLogin(), user5.getPassword());

        assertNotNull(token1);
        assertNotNull(token2);
        assertNotNull(token5);

        assertTrue(accountService.isLoggedIn(token1));
        assertTrue(accountService.isLoggedIn(token2));
        assertTrue(accountService.isLoggedIn(token5));
        assertFalse(accountService.isLoggedIn("wrong"));
        accountService.logoutUser(token1);
        accountService.logoutUser(token2);
        assertFalse(accountService.isLoggedIn(token1));
        assertFalse(accountService.isLoggedIn(token2));
        assertTrue(accountService.isLoggedIn(token5));
    }

    @Test
    public void testRemoveUserBySessionID() throws Exception {
        accountService.addUser(user1);
        accountService.addUser(user2);
        accountService.addUser(user3);
        accountService.addUser(user4);
        accountService.addUser(user5);

        final String token1 = accountService.loginUser(user1.getLogin(), user1.getPassword());
        final String token2 = accountService.loginUser(user2.getLogin(), user2.getPassword());
        final String token5 = accountService.loginUser(user5.getLogin(), user5.getPassword());
        assertNotNull(token1);
        assertNotNull(token2);
        assertNotNull(token5);

        accountService.removeUser(token1);
        assertNull(accountService.getUserByLogin(user1.getLogin()));
        assertEquals(4, accountService.countUsers());

        accountService.removeUser("wrong");
        assertEquals(4, accountService.countUsers());

        accountService.logoutUser(token5);
        accountService.removeUser(token5);
        assertEquals(4, accountService.countUsers());
    }

    @Test
    @Ignore
    public void testUpdateUserBySessionID() throws Exception {
        accountService.addUser(user1);
        final Long id2 = accountService.addUser(user2);
        accountService.addUser(user3);
        accountService.addUser(user4);
        final Long id5 = accountService.addUser(user5);

        assertNotNull(id2);
        assertNotNull(id5);
        final String token1 = accountService.loginUser(user1.getLogin(), user1.getPassword());
        final String token2 = accountService.loginUser(user2.getLogin(), user2.getPassword());
        final String token5 = accountService.loginUser(user5.getLogin(), user5.getPassword());
        assertNotNull(token1);
        assertNotNull(token2);
        assertNotNull(token5);

        user6.setLogin("new");
        accountService.updateUser(token2, user6);
        final UserProfile profile2 = accountService.getUserByLogin(user6.getLogin());
        assertNotNull(profile2);
        assertEquals("special", profile2.getPassword());
        assertEquals(id2, profile2.getId());

        user6.setLogin("newnew");
        accountService.updateUser("wrong", user6);

        accountService.logoutUser(token5);
        accountService.updateUser(token5, user6);
        UserProfile profile5 = accountService.getUserByLogin(user6.getLogin());
        assertNull(profile5);
        profile5 = accountService.getUserByID(id5);
        assertNotNull(profile5);
        assertNotEquals("special", profile5.getPassword());
    }

    @Test
    public void testGetUserBySessionID() throws Exception {
        accountService.addUser(user1);
        accountService.addUser(user2);
        accountService.addUser(user3);
        accountService.addUser(user4);
        accountService.addUser(user5);

        final String token1 = accountService.loginUser(user1.getLogin(), user1.getPassword());
        final String token2 = accountService.loginUser(user2.getLogin(), user2.getPassword());
        final String token5 = accountService.loginUser(user5.getLogin(), user5.getPassword());
        assertNotNull(token1);
        assertNotNull(token2);
        assertNotNull(token5);

        final UserProfile profile2 = accountService.getUserBySessionID(token2);
        final UserProfile profile5 = accountService.getUserBySessionID(token5);
        assertNotNull(profile2);
        assertNotNull(profile5);
        assertEquals("login2", profile2.getLogin());
        assertEquals("login5", profile5.getLogin());
        assertNull(accountService.getUserBySessionID("wrong"));
    }

    @After
    public void tearDown() {
        accountService.sessionFactory.close();
    }

}
