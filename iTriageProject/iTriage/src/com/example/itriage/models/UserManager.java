package com.example.itriage.models;

import android.widget.CheckBox;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * This class manages nurse and physician credentials and
 * the current logged-in user state.
 */
public class UserManager {

    public enum UserType {
        NURSE, PHYSICIAN
    }

    // The type of the logged-in user
    private UserType currentUserType;

    // A map of username -> password
    private Map<String, User> mCredentials;

    /**
     * A user
     */
    public static class User {
        private UserType userType;
        private String username;
        private String password;

        public User(UserType userType, String username, String password) {
            this.userType = userType;
            this.username = username;
            this.password = password;
        }

        public UserType getUserType() {
            return userType;
        }

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }
    }

    /**
     * Since this is a singleton, a new instance is created and a reference
     * is stored as soon as the class is loaded.
     */
    private static final UserManager INSTANCE = new UserManager();

    /**
     * Returns the instance of this UserManager class.
     *
     * @return the instance of this UserManager class.
     */
    public static UserManager getInstance() {
        return INSTANCE;
    }

    /**
     * Constructs this UserManager with no user set.
     */
    private UserManager() {
        mCredentials = new HashMap<String, User>();
    }

    /**
     * Sets the current user.
     *
     * @param username the username of the user
     */
    public void setCurrentUser(String username) {
        currentUserType = mCredentials.get(username).userType;
    }

    /**
     * Gets the current user.
     *
     * @return the current user
     */
    public UserType getCurrentUserType() {
        return currentUserType;
    }

    /**
     * Returns true if the given username is valid.
     *
     * @param username a username
     * @return true if the given username is valid
     */
    public boolean isUsernameValid(String username) {
        return mCredentials != null &&
               mCredentials.containsKey(username);
    }

    /**
     * Returns true if the given username and password are valid.
     *
     * @param username a username
     * @param password a password
     * @return true if the given username and password are valid
     */
    public boolean isPasswordValid(String username, String password) {
        return isUsernameValid(username) &&
               mCredentials.get(username).getPassword().equals(password);
    }

    /**
     * Loads the credentials file from the specified input stream.
     *
     * @param is an input stream
     * @throws IOException
     */
    public void loadCredentials(InputStream is) throws IOException {
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        String line = br.readLine();
        while (line != null) {
            String[] credentials = line.split(":");
            UserType userType = null;
            if (credentials.length > 0) {
                if (credentials[0].equals("nurse"))
                    userType = UserType.NURSE;
                else if (credentials[0].equals("physician"))
                    userType = UserType.PHYSICIAN;
                User user = new User(userType, credentials[1], credentials[2]);
                mCredentials.put(user.getUsername(), user);
            }
            line = br.readLine();
        }
    }

    /**
     * Writes a new credentials file to the specified output stream.
     *
     * @param os an output stream
     * @param defaultCredentials an array of credentials
     * @throws IOException
     */
    public void createNewCredentialsFile(OutputStream os, String[] defaultCredentials) throws IOException {
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));
        for (String credentials : defaultCredentials) {
            bw.write(credentials);
            bw.newLine();
        }
        bw.close();
    }

    /**
     * Adds new credentials to the specified output stream.
     *
     * @param os an output stream
     * @param isPhysician true iff the new user is a physician
     * @param newUsername the new username
     * @param newPassword the new password
     * @throws IOException
     */
    public void addCredentialsToFile(OutputStream os, boolean isPhysician, String newUsername, String newPassword) throws IOException {
        String line = String.format("%s:%s:%s", isPhysician ? "physician" : "nurse", newUsername, newPassword);

        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));
        bw.write(line);
        bw.newLine();
        bw.close();
    }

}
