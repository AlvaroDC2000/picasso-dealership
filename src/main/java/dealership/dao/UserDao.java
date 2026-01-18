package dealership.dao;

import dealership.controllers.RegisterRepairController.IdName;
import dealership.model.AuthUser;
import dealership.util.DbConnection;
import dealership.model.MechanicSkillRow;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;


/**
 * Data Access Object for user-related operations.
 * <p>
 * This DAO contains the database logic for authentication and for retrieving
 * mechanic data used across the application (combo boxes, boss views, and skills
 * editing). It also includes permission checks based on boss dealership in the
 * boss mechanic skills flow.
 * </p>
 */
public class UserDao {

    /**
     * SQL query used for user authentication (login).
     * <p>
     * It returns the user ID and role name for active users matching username
     * and password hash.
     * </p>
     */
    private static final String SQL_LOGIN =
            "SELECT u.id, r.name AS role_name " +
            "FROM `user` u " +
            "JOIN role r ON r.id = u.role_id " +
            "WHERE u.username = ? " +
            "  AND u.password_hash = ? " +
            "  AND u.is_active = 1";

    /**
     * SQL query used to retrieve active mechanics (for combo boxes).
     */
    private static final String SQL_ACTIVE_MECHANICS =
            "SELECT u.id, u.full_name " +
            "FROM `user` u " +
            "JOIN role r ON r.id = u.role_id " +
            "WHERE u.is_active = 1 " +
            "  AND UPPER(r.name) = 'MECHANIC' " +
            "ORDER BY u.full_name ASC";

    /**
     * Authenticates a user using username and password.
     * <p>
     * If credentials are valid and the user is active, it returns an {@link AuthUser}
     * containing the user ID and role name. Otherwise, it returns null.
     * </p>
     *
     * @param username the username entered in the login form
     * @param password the password entered in the login form
     * @return an {@link AuthUser} if authentication is successful, or null if invalid
     * @throws Exception if a database access error occurs
     */
    public AuthUser authenticate(String username, String password) throws Exception {

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_LOGIN)) {

            ps.setString(1, username);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int userId = rs.getInt("id");
                    String roleName = rs.getString("role_name");
                    return new AuthUser(userId, roleName != null ? roleName.trim() : "");
                }
            }
        }

        return null;
    }

    /**
     * Retrieves a list of active mechanics formatted for combo boxes.
     * <p>
     * Each mechanic is returned as an {@link IdName} pair with the user ID and full name.
     * If the name is missing, a fallback label is used.
     * </p>
     *
     * @return list of active mechanics for selection controls
     * @throws Exception if a database access error occurs
     */
    public List<IdName> findActiveMechanicsForCombo() throws Exception {

        List<IdName> mechanics = new ArrayList<>();

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_ACTIVE_MECHANICS);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String fullName = rs.getString("full_name");
                mechanics.add(new IdName(id, fullName != null ? fullName.trim() : ("Mechanic #" + id)));
            }
        }

        return mechanics;
    }
    
    /**
     * Retrieves mechanics from the same dealership as the given boss, including their skills.
     * <p>
     * This method is used by the boss skills overview screen. The boss ID is used to
     * resolve the dealership and filter mechanics accordingly.
     * </p>
     *
     * @param bossId the boss user ID used to determine dealership scope
     * @return list of mechanics with skills and active status text
     * @throws Exception if a database access error occurs
     */
    public List<MechanicSkillRow> findMechanicsWithSkillsForBossDealership(int bossId) throws Exception {

        String sql =
                "SELECT m.id, m.full_name, m.skills, m.is_active " +
                "FROM `user` m " +
                "JOIN role r ON r.id = m.role_id " +
                "JOIN `user` b ON b.id = ? " +
                "WHERE r.name = 'MECHANIC' " +
                "  AND m.dealership_id = b.dealership_id " +
                "ORDER BY m.full_name ASC";

        List<MechanicSkillRow> list = new ArrayList<>();

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, bossId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String name = rs.getString("full_name");
                    String skills = rs.getString("skills");
                    boolean active = rs.getBoolean("is_active");

                    list.add(new MechanicSkillRow(
                            id,
                            name != null ? name.trim() : "",
                            skills != null ? skills.trim() : "",
                            active ? "Active" : "Inactive"
                    ));
                }
            }
        }

        return list;
    }


    /**
     * Retrieves the skills text for a specific mechanic within the boss dealership.
     * <p>
     * The boss ID is used to validate that the mechanic belongs to the same dealership.
     * If the mechanic is not found or the boss has no permissions, this method returns null.
     * </p>
     *
     * @param bossId the boss user ID used for dealership validation
     * @param mechanicId the mechanic user ID whose skills will be loaded
     * @return the skills text (trimmed), empty string if null in DB, or null if not found/no permissions
     * @throws Exception if a database access error occurs
     */
    public String findMechanicSkillsForBossDealership(int bossId, int mechanicId) throws Exception {

        String sql =
                "SELECT m.skills " +
                "FROM `user` m " +
                "JOIN role r ON r.id = m.role_id " +
                "JOIN `user` b ON b.id = ? " +
                "WHERE m.id = ? " +
                "  AND r.name = 'MECHANIC' " +
                "  AND m.dealership_id = b.dealership_id";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, bossId);
            stmt.setInt(2, mechanicId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String skills = rs.getString("skills");
                    return skills != null ? skills.trim() : "";
                }
            }
        }

        return null;
    }


    /**
     * Updates the skills text for a mechanic within the boss dealership.
     * <p>
     * The update only happens if the target user is a mechanic and belongs to the same
     * dealership as the boss. The method returns true when a row was actually updated.
     * </p>
     *
     * @param bossId the boss user ID used for dealership validation
     * @param mechanicId the mechanic user ID to update
     * @param skills the new skills text to store
     * @return true if the update affected at least one row, false otherwise
     * @throws Exception if a database access error occurs
     */
    public boolean updateMechanicSkillsForBossDealership(int bossId, int mechanicId, String skills) throws Exception {

        String sql =
                "UPDATE `user` m " +
                "JOIN role r ON r.id = m.role_id " +
                "JOIN `user` b ON b.id = ? " +
                "SET m.skills = ? " +
                "WHERE m.id = ? " +
                "  AND r.name = 'MECHANIC' " +
                "  AND m.dealership_id = b.dealership_id";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, bossId);
            stmt.setString(2, skills);
            stmt.setInt(3, mechanicId);

            return stmt.executeUpdate() > 0;
        }
    }

}
