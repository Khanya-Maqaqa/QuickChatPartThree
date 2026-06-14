
package com.mycompany.poepartthree;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *  Login Class handles user registration and authentication
 * @author kvmaq
 */
public class Login {
    
    // Stored credentials set during registration
    private String storedUsername;
    private String storedPassword;
    private String storedPhonenumber;
    private String storedName;
    private String storedSurname;
    
    // Regex: min chars, 1 uppercase, 1 digit, 1 special character
    private static String PATTERN_PASSWORD ="^(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%^&*(),.?\":{}|<>]).{8,}$";

    // Regex: +27 followed by exactly 9 digits
    private static  String PHONE_PATTERN = "^\\+27\\d{9}$";
    
    //Constructor: stores the user's details captured during registration
    public Login(String username, String password, String phonenumber,String name, String surname) {
      this.storedUsername = username;
      this.storedPassword = password;
      this.storedPhonenumber = phonenumber;
      this.storedName = name;
      this.storedSurname = surname;
    }
    
    //Checks that the username contains an underscore and has max 5 characters
    public boolean checkUserName(String username) {
        if (username != null && username.length() <= 5 && username.contains("_")) {
            System.out.println("Username successfully captured");
            return true;
        } else {
            System.out.println("Username is not correctly formatted; please ensure that "+ "your username contains an underscore and is no more than five "+ "characters in length.");
            return false;
        }
    }
    
    //Checks password complexity: min 8 chars, 1 uppercase, 1 digit, 1 special char
    public boolean checkPasswordComplexity(String password) {
        if (password == null) {
            System.out.println("Password is not correctly formatted; please ensure that " + "the password contains at least eight characters, a capital letter, " + "a number and a special character.");
            return false;
        }
        Pattern pattern = Pattern.compile(PATTERN_PASSWORD);
        Matcher matcher = pattern.matcher(password);
        if (matcher.matches()) {
            System.out.println("Password successfully captured.");
            return true;
        } else {
            System.out.println("Password is not correctly formatted; please ensure that " + "the password contains at least eight characters, a capital letter, " + "a number and a special character.");
            return false;
        }
    }
    
    //Checks that the phone number starts with +27 followed by exactly 9 digits
    public boolean checkCellPhoneNumber(String phonenumber) {
        if (phonenumber == null) {
            System.out.println("Cell phone number incorrectly formatted or does not " + "contain international code (+27).");
            return false;
        }
        Pattern pattern = Pattern.compile(PHONE_PATTERN);
        Matcher matcher = pattern.matcher(phonenumber);
        if (matcher.matches()) {
            System.out.println("Cell phone number successfully added.");
            return true;
        } else {
            System.out.println("Cell phone number incorrectly formatted or does not " + "contain international code (+27).");
            return false;
        }
    }
    
    //Validates username, password, and phone number
    public String registerUser(String username, String password, String phonenumber) {
        //Intializing Boolean variables as true if username, password and phonenumber are valid
        boolean validUsername = checkUserName(username);
        boolean validPassword = checkPasswordComplexity(password);
        boolean validPhone    = checkCellPhoneNumber(phonenumber);

        if (!validUsername) {
            return "Registration failed: invalid username.";
        }
        if (!validPassword) {
            return "Registration failed: invalid password.";
        }
        if (!validPhone) {
            return "Registration failed: invalid cell phone number.";
        }

        // Assign the local variable from the global
        this.storedUsername    = username;
        this.storedPassword    = password;
        this.storedPhonenumber = phonenumber;

        return "User successfully registered.";
    }

    
    // Verifies that the typed credentials by user match the stored ones when registering.
    public boolean loginUser(String username, String password) {
        return storedUsername != null && storedPassword != null && storedUsername.equals(username) && storedPassword.equals(password);
    }

    //Returns a login status message.
    public String returnLoginStatus(String username, String password) {
        if (loginUser(username, password)) {
            return "Welcome " + storedName + " " + storedSurname + " it is great to see you again.";
        } else {
            return "Username or password incorrect, please try again.";
        }
    }

    //Getters
    public String getStoredName(){ 
        return storedName; 
    }
    public String getStoredSurname(){ 
        return storedSurname; 
    }
    public String getStoredUsername(){ 
        return storedUsername; 
    }
}


