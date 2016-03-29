package main;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by KOPTE3 on 24.03.2016.
 */
public class AccountServiceTest {

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testGetAllUsers() throws Exception {

    }

    @Test
    public void testGetAllActiveUsers() throws Exception {

    }

    @Test
    public void testCountUsers() throws Exception {
        final AccountServiceImpl service = new AccountServiceImpl();
        final int countUsers = service.countUsers();

        assertEquals(0, countUsers);
    }

    @Test
    public void testAddUser() throws Exception {

    }

    @Test
    public void testGetUser() throws Exception {

    }
}