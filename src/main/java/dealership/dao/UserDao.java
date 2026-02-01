package dealership.dao;

import dealership.controllers.RegisterRepairController.IdName;
import dealership.model.AuthUser;
import dealership.model.MechanicSkillRow;
import dealership.util.DbConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for user-related operations.
 * <p>
 * This class contains database queries related to authentication and mechanics management.
 * It is used by:
 * - Login: authenticate user and get role/dealership data.
 * - Boss mechanic screens: list mechanics, read skills, and update skills.
 * </p>
 */
public class UserDao {

    /**
     * SQL query used for user authentication (login).
     * <p>
     * It returns the user ID, dealership ID and role name for active users matching username
     * and password hash.
     * </p>
     */
    private static final String SQL_LOGIN =
            "SELECT u.id, u.dealership_id, r.name AS role_name " +
            "FROM `user` u " +
            "JOIN role r ON r.id = u.role_id " +
            "WHERE u.username = ? " +
            "  AND u.password_hash = ? " +
            "  AND u.is_active = 1";

    /**
     * SQL query used to retrieve active mechanics for combo boxes.
     */
    private static final String SQL_ACTIVE_MECHANICS =
            "SELECT u.id, u.full_name " +
            "FROM `user` u " +
            "JOIN role r ON r.id = u.role_id " +
            "WHERE u.is_active = 1 " +
            "  AND UPPER(r.name) = 'MECHANIC' " +
            "ORDER BY u.full_name ASC";

    /**
     * Authenticates a user by username and password.
     * <p>
     * If a matching active user is found, it returns an {@link AuthUser} with:
     * - user id
     * - dealership id
     * - role name
     * </p>
     *
     * @param username username entered in the login form
     * @param password password entered in the login form
     * @return AuthUser if credentials are valid, null otherwise
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
                    int dealershipId = rs.getInt("dealership_id"); // 0 if NULL, but it should not be NULL
                    String roleName = rs.getString("role_name");
                    return new AuthUser(
                            userId,
                            dealershipId,
                            roleName != null ? roleName.trim() : ""
                    );
                }
            }
        }

        return null;
    }

    /**
     * Returns all active mechanics for UI combo boxes.
     * <p>
     * Each item is formatted as {@link IdName}, where:
     * - id = mechanic user id
     * - name = mechanic full name (fallback: "Mechanic #id")
     * </p>
     *
     * @return list of active mechanics as IdName
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
     * Retrieves mechanics from the same dealership as the boss user.
     * <p>
     * This method is used for Boss -> Mechanics and skills list.
     * It includes the mechanic skills and active/inactive status.
     * </p>
     *
     * @param bossId boss user id (used to resolve dealership)
     * @return list of mechanics with skills and status
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
     * Loads the skills text for a mechanic, but only if the boss and mechanic
     * belong to the same dealership.
     *
     * @param bossId boss user id (used to validate dealership permissions)
     * @param mechanicId mechanic user id to load
     * @return skills text (trimmed) or empty string if skills is NULL, or null if not found / no permissions
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
     * Updates a mechanic skills text, but only if the boss and mechanic belong
     * to the same dealership.
     *
     * @param bossId boss user id (used to validate dealership permissions)
     * @param mechanicId mechanic user id to update
     * @param skills new skills text to save
     * @return true if updated, false if no rows were updated (not found or no permissions)
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
