package dealership.model;

/**
 * Simple model representing an authenticated user.
 * <p>
 * This class is used after a successful login to store the minimum
 * information required to initialize a user session: the user ID
 * and the role name associated with that user.
 * </p>
 */
public class AuthUser {

    private final int id;
    private final String roleName;

    /**
     * Creates a new authenticated user instance.
     *
     * @param id the unique identifier of the user
     * @param roleName the role name assigned to the user
     */
    public AuthUser(int id, String roleName) {
        this.id = id;
        this.roleName = roleName;
    }

    /**
     * Returns the user identifier.
     *
     * @return the user ID
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the role name of the user.
     *
     * @return the role name
     */
    public String getRoleName() {
        return roleName;
    }
}

