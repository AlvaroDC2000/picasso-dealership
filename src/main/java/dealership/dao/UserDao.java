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


public class UserDao {

    private static final String SQL_LOGIN =
            "SELECT u.id, r.name AS role_name " +
            "FROM `user` u " +
            "JOIN role r ON r.id = u.role_id " +
            "WHERE u.username = ? " +
            "  AND u.password_hash = ? " +
            "  AND u.is_active = 1";

    private static final String SQL_ACTIVE_MECHANICS =
            "SELECT u.id, u.full_name " +
            "FROM `user` u " +
            "JOIN role r ON r.id = u.role_id " +
            "WHERE u.is_active = 1 " +
            "  AND UPPER(r.name) = 'MECHANIC' " +
            "ORDER BY u.full_name ASC";

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




