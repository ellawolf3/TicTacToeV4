package clarkson.ee408.tictactoev4.model;

/**
 * Models a user that will play a game
 */
public class User {
    private String username;
    private String password;
    private String displayName;
    private boolean online;

    /**
     * Default constructor for User class.
     * Initializes username, password, displayName, and online status to default values.
     */
    public User() {
        this.username = null;
        this.password = null;
        this.displayName = null;
        this.online = false;
    }

    /**
     * Constructor for creating a User with specified attributes.
     *
     * @param username    The username of the user.
     * @param password    The password of the user.
     * @param displayName The display name of the user.
     * @param online      Indicates whether the user is currently online.
     */
    public User(String username, String password, String displayName, boolean online) {
        this.username = username;
        this.password = password;
        this.displayName = displayName;
        this.online = online;
    }

    /**
     * Gets the username of the user.
     *
     * @return The username.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username of the user.
     *
     * @param username The new username.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Gets the password of the user.
     *
     * @return The password.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password of the user.
     *
     * @param password The new password.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Gets the display name of the user.
     *
     * @return The display name.
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Sets the display name of the user.
     *
     * @param displayName The new display name.
     */
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Checks if the user is currently online.
     *
     * @return true if the user is online, false otherwise.
     */
    public boolean getOnline() {
        return online;
    }

    /**
     * Sets the online status of the user.
     *
     * @param online true to mark the user as online, false otherwise.
     */
    public void setOnline(boolean online) {
        this.online = online;
    }

    /**
     * Compares this user to another user based on their usernames.
     *
     * @param obj The object to compare to.
     * @return true if the usernames are equal, false otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof User)) {
            return false;
        }
        User otherUser = (User) obj;

        return this.username.equals(otherUser.username);
    }
}
