package dealership.util;

public class SessionContext {

    private static Integer userId;
    private static String roleName;

    public static Integer getUserId() {
        return userId;
    }

    public static void setUserId(Integer userId) {
        SessionContext.userId = userId;
    }

    public static String getRoleName() {
        return roleName;
    }

    public static void setRoleName(String roleName) {
        SessionContext.roleName = roleName;
    }

    public static void clear() {
        userId = null;
        roleName = null;
    }
}
