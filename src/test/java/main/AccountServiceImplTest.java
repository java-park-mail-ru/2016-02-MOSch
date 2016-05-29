package main;

import org.junit.Before;
import org.junit.Test;
import rest.UserProfile;
import supportclasses.LoginScoreSet;

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
    AccountServiceImpl accountService = new AccountServiceImpl("test_hibernate.cfg.xml");

    @Before
    public void setUp() {
        accountService.removeAll();
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

        final UserProfile user7 = new UserProfile("login7", "1111");
        final UserProfile user8 = new UserProfile("  login", "password");
        final UserProfile user9 = new UserProfile("23__ sdwq", " wqer");
        final Long id7 = accountService.addUser(user7);
        final Long id8 = accountService.addUser(user8);
        final Long id9 = accountService.addUser(user9);

        assertNull(id7);
        assertNull(id8);
        assertNull(id9);
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

        assertEquals("login1", userProfile1.getUsername());
        assertEquals("login2", userProfile2.getUsername());
    }

    @Test
    public void testGetUserByLogin() throws Exception {
        accountService.addUser(user1);
        accountService.addUser(user2);

        final UserProfile userProfile1 = accountService.getUserByLogin(user1.getUsername());
        final UserProfile userProfile2 = accountService.getUserByLogin(user2.getUsername());
        final UserProfile profileWrong = accountService.getUserByLogin("wrong");

        assertNotNull(userProfile1);
        assertNotNull(userProfile2);
        assertNull(profileWrong);

        assertEquals("login1", userProfile1.getUsername());
        assertEquals("login2", userProfile2.getUsername());
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
    public void testUpdateUser() throws Exception {
        final Long id1 = accountService.addUser(user1);
        final Long id2 = accountService.addUser(user2);

        assertNotNull(id1);
        assertNotNull(id2);
        final Long newScore = 1000L;
        user3.setScore(newScore);
        assertTrue(accountService.updateUser(id2, user3));
        final UserProfile newUser2 = accountService.getUserByID(id2);
        assertNotNull(newUser2);
        assertEquals(newScore, newUser2.getScore());
    }

    @Test
    public void testIsUserExists() throws Exception {
        accountService.addUser(user1);
        accountService.addUser(user2);
        accountService.addUser(user3);
        accountService.addUser(user4);
        accountService.addUser(user5);

        assertNotNull(accountService.getUserID(user2.getUsername(), user2.getPassword()));
        assertNull(accountService.getUserID(user2.getUsername(), "wrong"));
        assertNull(accountService.getUserID("wrong", "wrong"));
    }

    @Test
    public void testGetAllUsers() {
        accountService.addUser(user1);
        accountService.addUser(user2);
        accountService.addUser(user3);
        accountService.addUser(user4);
        accountService.addUser(user5);

        final List<LoginScoreSet> users = accountService.getTopUsers();
        assertNotNull(users);
        assertEquals(5, users.size());
        assertTrue(users.get(0).username.equals("login1")
                && users.get(1).username.equals("login2")
                && users.get(2).username.equals("login3")
                && users.get(3).username.equals("login4")
                && users.get(4).username.equals("login5"));
    }

    @Test
    public void testLoginUser() throws Exception {
        accountService.addUser(user1);
        accountService.addUser(user2);
        accountService.addUser(user3);
        accountService.addUser(user4);
        accountService.addUser(user5);

        final Boolean token1 = accountService.loginUser(user1.getUsername(), user1.getPassword(), "session1");
        final Boolean token2 = accountService.loginUser(user2.getUsername(), user2.getPassword(), "session2");
        final Boolean token5 = accountService.loginUser(user5.getUsername(), user5.getPassword(), "session5");
        assertNotNull(token1);
        assertNotNull(token2);
        assertNotNull(token5);

        final Boolean token1new = accountService.loginUser(user1.getUsername(), user1.getPassword(), "session1new");

        assertTrue(token1new);
        final UserProfile user = accountService.getUserBySessionID("session1new");
        assertNotNull(user);
        assertEquals(user1.getUsername(), user.getUsername());
        assertEquals(user1.getPassword(), user.getPassword());
    }

    @Test
    public void testLogoutUser() throws Exception {
        accountService.addUser(user1);
        accountService.addUser(user2);
        accountService.addUser(user3);
        accountService.addUser(user4);
        accountService.addUser(user5);

        final Boolean token1 = accountService.loginUser(user1.getUsername(), user1.getPassword(), "session1");
        final Boolean token2 = accountService.loginUser(user2.getUsername(), user2.getPassword(), "session2");
        final Boolean token5 = accountService.loginUser(user5.getUsername(), user5.getPassword(), "session5");
        assertNotNull(token1);
        assertNotNull(token2);
        assertNotNull(token5);

        accountService.logoutUser("session1");
        accountService.logoutUser("session2");
        accountService.logoutUser("wrong");
    }

    @Test
    public void testIsLoggedIn() throws Exception {
        accountService.addUser(user1);
        accountService.addUser(user2);
        accountService.addUser(user3);
        accountService.addUser(user4);
        accountService.addUser(user5);

        final Boolean token1 = accountService.loginUser(user1.getUsername(), user1.getPassword(), "session1");
        final Boolean token2 = accountService.loginUser(user2.getUsername(), user2.getPassword(), "session2");
        final Boolean token5 = accountService.loginUser(user5.getUsername(), user5.getPassword(), "session5");

        assertTrue(token1);
        assertTrue(token2);
        assertTrue(token5);

        assertNotNull(accountService.getUserBySessionID("session1"));
        assertNotNull(accountService.getUserBySessionID("session2"));
        assertNotNull(accountService.getUserBySessionID("session5"));
        assertNull(accountService.getUserBySessionID("wrong"));

        accountService.logoutUser("session1");
        accountService.logoutUser("session2");
        assertNull(accountService.getUserBySessionID("session1"));
        assertNull(accountService.getUserBySessionID("session2"));
        assertNotNull(accountService.getUserBySessionID("session5"));
    }

    @Test
    public void testRemoveUserBySessionID() throws Exception {
        accountService.addUser(user1);
        accountService.addUser(user2);
        accountService.addUser(user3);
        accountService.addUser(user4);
        accountService.addUser(user5);

        final Boolean token1 = accountService.loginUser(user1.getUsername(), user1.getPassword(), "session1");
        final Boolean token2 = accountService.loginUser(user2.getUsername(), user2.getPassword(), "session2");
        final Boolean token5 = accountService.loginUser(user5.getUsername(), user5.getPassword(), "session5");
        assertTrue(token1);
        assertTrue(token2);
        assertTrue(token5);

        accountService.removeUser("session1");
        assertNull(accountService.getUserByLogin(user1.getUsername()));
        assertEquals(4, accountService.countUsers());

        accountService.removeUser("wrong");
        assertEquals(4, accountService.countUsers());

        accountService.logoutUser("session5");
        accountService.removeUser("session5");
        assertEquals(4, accountService.countUsers());
    }


    @Test
    public void testUpdateUserBySessionID() throws Exception {
        accountService.addUser(user1);
        final Long id2 = accountService.addUser(user2);
        accountService.addUser(user3);
        accountService.addUser(user4);
        final Long id5 = accountService.addUser(user5);

        assertNotNull(id2);
        assertNotNull(id5);
        final Boolean token1 = accountService.loginUser(user1.getUsername(), user1.getPassword(), "session1");
        final Boolean token2 = accountService.loginUser(user2.getUsername(), user2.getPassword(), "session2");
        final Boolean token5 = accountService.loginUser(user5.getUsername(), user5.getPassword(), "session5");
        assertTrue(token1);
        assertTrue(token2);
        assertTrue(token5);

        final Long newScore = 1000L;
        user6.setScore(newScore);
        accountService.updateUser("session2", user6);
        final UserProfile profile2 = accountService.getUserByID(id2);
        assertNotNull(profile2);
        assertEquals(newScore, profile2.getScore());
        assertEquals(id2, profile2.getId());

        final Long newScore2 = 2000L;
        user6.setScore(newScore2);
        accountService.updateUser("wrong", user6);
        final UserProfile profile3 = accountService.getUserByID(id2);
        assertNotNull(profile3);
        assertNotEquals(newScore2, profile3.getScore());

        final Long newScore3 = 3000L;
        user6.setScore(newScore3);
        accountService.logoutUser("session5");
        accountService.updateUser("session5", user6);
        final UserProfile profile5 = accountService.getUserByID(id5);
        assertNotNull(profile5);
        assertNotEquals(newScore3, profile5.getScore());
    }

    @Test
    public void testGetUserBySessionID() throws Exception {
        accountService.addUser(user1);
        accountService.addUser(user2);
        accountService.addUser(user3);
        accountService.addUser(user4);
        accountService.addUser(user5);

        final Boolean token1 = accountService.loginUser(user1.getUsername(), user1.getPassword(), "session1");
        final Boolean token2 = accountService.loginUser(user2.getUsername(), user2.getPassword(), "session2");
        final Boolean token5 = accountService.loginUser(user5.getUsername(), user5.getPassword(), "session5");
        assertTrue(token1);
        assertTrue(token2);
        assertTrue(token5);

        final UserProfile profile2 = accountService.getUserBySessionID("session2");
        final UserProfile profile5 = accountService.getUserBySessionID("session5");
        assertNotNull(profile2);
        assertNotNull(profile5);
        assertEquals("login2", profile2.getUsername());
        assertEquals("login5", profile5.getUsername());
        assertNull(accountService.getUserBySessionID("wrong"));
    }
}
