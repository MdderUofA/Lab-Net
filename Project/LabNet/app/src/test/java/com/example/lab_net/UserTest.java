package com.example.lab_net;


import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Testing User class.
 */
public class UserTest {

    @Test
    public void testGetUserId() {
        User user = new User("testing123","tom","jerry",
                                "tom@gmail.com","1234567890");
        assertEquals("testing123",user.getUserId());
    }

    @Test
    public void testGetFirstName() {
        User user = new User("testing123","tom","jerry",
                "tom@gmail.com","1234567890");
        assertEquals("tom",user.getFirstName());
    }

    @Test
    public void testSetFirstName() {
        User user = new User("testing123","tom","jerry",
                "tom@gmail.com","1234567890");
        user.setFirstName("john");
        assertEquals("john", user.getFirstName());
    }

    @Test
    public void testGetLastName() {
        User user = new User("testing123","tom","jerry",
                "tom@gmail.com","1234567890");
        assertEquals("jerry", user.getLastName());
    }

    @Test
    public void testSetLastName() {
        User user = new User("testing123","tom","jerry",
                "tom@gmail.com","1234567890");
        user.setLastName("davis");
        assertEquals("davis", user.getLastName());
    }

    @Test
    public void testGetEmail() {
        User user = new User("testing123","tom","jerry",
                "tom@gmail.com","1234567890");
        assertEquals("tom@gmail.com", user.getEmail());
    }

    @Test
    public void testSetEmail() {
        User user = new User("testing123","tom","jerry",
                "tom@gmail.com","1234567890");
        user.setEmail("jerry@gmail.com");
        assertEquals("jerry@gmail.com", user.getEmail());
    }

    @Test
    public void testGetPhone() {
        User user = new User("testing123","tom","jerry",
                "tom@gmail.com","1234567890");
        assertEquals("1234567890", user.getPhone());
    }

    @Test
    public void testSetPhone() {
        User user = new User("testing123","tom","jerry",
                "tom@gmail.com","1234567890");
        user.setPhone("9876543210");
        assertEquals("9876543210", user.getPhone());
    }
}