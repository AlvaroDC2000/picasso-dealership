package dealership.model;

public class AuthUser {

    private final int id;
    private final String roleName;

    public AuthUser(int id, String roleName) {
        this.id = id;
        this.roleName = roleName;
    }

    public int getId() {
        return id;
    }

    public String getRoleName() {
        return roleName;
    }
}
