package com.example.project_iot.database;

public interface IDatabaseHelper {

    /**
     * Opens connection
     * @return true if connection established
     */
    public boolean open();

    /**
     * Closes connection
     */
    public void close();

    /**
     * Creates user
     * @param username
     * @param password
     * @return created user id
     */
    public int insert(String username, String password);

    /**
     * Checks if user exists
     * @param username
     * @return true if user already exists
     */
    public boolean isUsernameTaken(String username);

    /**
     * Checks user credentials
     * @param username
     * @param password
     * @return true if user credentials are correct
     */
    public boolean checkLogin(String username, String password);

}
