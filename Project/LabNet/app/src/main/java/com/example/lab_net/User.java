/**
 * CMPUT 301
 * @version 1.0
 * March 19, 2021
 *
 */
package com.example.lab_net;


/**
 * This class defines the user and its attributes.
 *
 * @author Qasim Akhtar
 */
public class User {

    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String userId;


    public User(String userId, String firstName, String lastName, String email, String phone) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
    }

    public String getUserId() {
        return userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

}
