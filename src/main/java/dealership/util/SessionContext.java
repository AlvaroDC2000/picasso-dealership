package dealership.util;

/**
 * Utility class used to store session-related information.
 * <p>
 * This context keeps track of the currently logged-in user by storing
 * the user ID, dealership ID and role name in memory. It is accessed statically from
 * different controllers to determine permissions and navigation flow.
 * </p>
 */
public class SessionContext {

    private static Integer userId;
    private static Integer dealershipId;
    private static String roleName;

    private SessionContext() {
        // Utility class
    }

    /**
     * Returns the current user ID stored in the session.
     *
     * @return the user ID, or null if no session is active
     */
    public static Integer getUserId() {
        return userId;
    }

    /**
     * Sets the current user ID in the session.
     *
     * @param userId the user ID to store
     */
    public static void setUserId(Integer userId) {
        SessionContext.userId = userId;
    }

    /**
     * Returns the current dealership ID stored in the session.
     *
     * @return the dealership ID, or null if no session is active
     */
    public static Integer getDealershipId() {
        return dealershipId;
    }

    /**
     * Sets the current dealership ID in the session.
     *
     * @param dealershipId the dealership ID to store
     */
    public static void setDealershipId(Integer dealershipId) {
        SessionContext.dealershipId = dealershipId;
    }

    /**
     * Returns the role name of the current user.
     *
     * @return the role name, or null if no session is active
     */
    public static String getRoleName() {
        return roleName;
    }

    /**
     * Sets the role name of the current user.
     *
     * @param roleName the role name to store
     */
    public static void setRoleName(String roleName) {
        SessionContext.roleName = roleName;
    }

    /**
     * Clears all session data.
     * <p>
     * After calling this method, the application will behave as if
     * no user is logged in.
     * </p>
     */
    public static void clear() {
        userId = null;
        dealershipId = null;
        roleName = null;
    }
}



