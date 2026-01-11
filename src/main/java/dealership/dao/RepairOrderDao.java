package dealership.dao;

import dealership.model.BossRepairEditDetails;
import dealership.model.RepairDetails;
import dealership.model.RepairTaskRow;
import dealership.util.DbConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class RepairOrderDao {

    private static final String SQL_FIND_TASKS_BY_MECHANIC =
            "SELECT " +
            "   ro.id AS repair_id, " +
            "   CONCAT(v.brand, ' ', v.model) AS vehicle, " +
            "   ro.status AS status " +
            "FROM repair_order ro " +
            "JOIN vehicle v ON v.id = ro.vehicle_id " +
            "WHERE ro.assigned_mechanic_id = ? " +
            "ORDER BY ro.id ASC";

    private static final String SQL_FIND_REPAIRS_BY_BOSS =
            "SELECT " +
            "   ro.id AS repair_id, " +
            "   CONCAT(v.brand, ' ', v.model) AS vehicle, " +
            "   ro.status AS status " +
            "FROM repair_order ro " +
            "JOIN vehicle v ON v.id = ro.vehicle_id " +
            "WHERE ro.created_by_boss_id = ? " +
            "ORDER BY ro.id ASC";

    private static final String SQL_FIND_BOSS_EDIT_DETAILS_BY_ID =
            "SELECT " +
            "   ro.id AS repair_id, " +
            "   ro.status AS status, " +
            "   ro.notes AS notes, " +
            "   CONCAT(v.brand, ' ', v.model) AS vehicle_text, " +
            "   ro.assigned_mechanic_id AS mechanic_id, " +
            "   u.full_name AS mechanic_name " +
            "FROM repair_order ro " +
            "JOIN vehicle v ON v.id = ro.vehicle_id " +
            "LEFT JOIN `user` u ON u.id = ro.assigned_mechanic_id " +
            "WHERE ro.id = ? AND ro.created_by_boss_id = ?";

    private static final String SQL_ASSIGN_MECHANIC_AND_UPDATE_NOTES =
            "UPDATE repair_order " +
            "SET assigned_mechanic_id = ?, " +
            "    status = 'ASSIGNED', " +
            "    notes = ? " +
            "WHERE id = ? " +
            "  AND created_by_boss_id = ? " +
            "  AND UPPER(TRIM(status)) IN ('PENDING','ASSIGNED')";

    private static final String SQL_UNASSIGN_MECHANIC_AND_UPDATE_NOTES =
            "UPDATE repair_order " +
            "SET assigned_mechanic_id = NULL, " +
            "    status = 'PENDING', " +
            "    notes = ? " +
            "WHERE id = ? " +
            "  AND created_by_boss_id = ? " +
            "  AND UPPER(TRIM(status)) IN ('PENDING','ASSIGNED')";

    private static final String SQL_FIND_REPAIR_DETAILS_BY_ID =
            "SELECT " +
            "   ro.id AS repair_id, " +
            "   ro.status AS status, " +
            "   ro.notes AS notes, " +
            "   c.id AS customer_id, " +
            "   CONCAT(IFNULL(c.first_name,''), ' ', IFNULL(c.last_name,'')) AS customer_name, " +
            "   c.dni AS customer_dni, " +
            "   c.phone AS customer_phone, " +
            "   c.email AS customer_email, " +
            "   CONCAT(v.brand, ' ', v.model) AS vehicle_text " +
            "FROM repair_order ro " +
            "JOIN vehicle v ON v.id = ro.vehicle_id " +
            "JOIN customer c ON c.id = ro.customer_id " +
            "WHERE ro.id = ?";

    private static final String SQL_START_REPAIR =
            "UPDATE repair_order " +
            "SET status = 'IN_PROGRESS', start_at = COALESCE(start_at, NOW()) " +
            "WHERE id = ? AND UPPER(TRIM(status)) = 'ASSIGNED'";

    private static final String SQL_FINISH_REPAIR =
            "UPDATE repair_order " +
            "SET status = 'FINISHED', end_at = NOW() " +
            "WHERE id = ? AND UPPER(TRIM(status)) = 'IN_PROGRESS'";

    public List<RepairTaskRow> findTasksByMechanicId(int mechanicUserId) throws Exception {

        List<RepairTaskRow> tasks = new ArrayList<>();

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_FIND_TASKS_BY_MECHANIC)) {

            stmt.setInt(1, mechanicUserId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int repairId = rs.getInt("repair_id");
                    String vehicle = rs.getString("vehicle");
                    String status = rs.getString("status");
                    tasks.add(new RepairTaskRow(repairId, vehicle, status));
                }
            }
        }

        return tasks;
    }

    public List<RepairTaskRow> findRepairsByBossId(int bossId) throws Exception {

        List<RepairTaskRow> list = new ArrayList<>();

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_FIND_REPAIRS_BY_BOSS)) {

            stmt.setInt(1, bossId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int repairId = rs.getInt("repair_id");
                    String vehicle = rs.getString("vehicle");
                    String status = rs.getString("status");
                    list.add(new RepairTaskRow(repairId, vehicle, status));
                }
            }
        }

        return list;
    }

    public BossRepairEditDetails findBossEditDetailsById(int repairId, int bossId) throws Exception {

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_FIND_BOSS_EDIT_DETAILS_BY_ID)) {

            stmt.setInt(1, repairId);
            stmt.setInt(2, bossId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String status = rs.getString("status");
                    String notes = rs.getString("notes");
                    String vehicleText = rs.getString("vehicle_text");

                    int mechanicId = rs.getInt("mechanic_id");
                    Integer mechanicIdObj = rs.wasNull() ? null : mechanicId;
                    String mechanicName = rs.getString("mechanic_name");

                    return new BossRepairEditDetails(
                            repairId,
                            vehicleText != null ? vehicleText.trim() : "",
                            status != null ? status.trim() : "",
                            notes != null ? notes.trim() : "",
                            mechanicIdObj,
                            mechanicName != null ? mechanicName.trim() : ""
                    );
                }
            }
        }

        return null;
    }

    public boolean assignMechanicAndUpdateNotes(int repairId, int bossId, int mechanicId, String notes) throws Exception {

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_ASSIGN_MECHANIC_AND_UPDATE_NOTES)) {

            stmt.setInt(1, mechanicId);
            stmt.setString(2, notes);
            stmt.setInt(3, repairId);
            stmt.setInt(4, bossId);

            return stmt.executeUpdate() > 0;
        }
    }

    public boolean unassignMechanicAndUpdateNotes(int repairId, int bossId, String notes) throws Exception {

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_UNASSIGN_MECHANIC_AND_UPDATE_NOTES)) {

            stmt.setString(1, notes);
            stmt.setInt(2, repairId);
            stmt.setInt(3, bossId);

            return stmt.executeUpdate() > 0;
        }
    }

    public RepairDetails findRepairDetailsById(int repairId) throws Exception {

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_FIND_REPAIR_DETAILS_BY_ID)) {

            stmt.setInt(1, repairId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {

                    String status = rs.getString("status");
                    String notes = rs.getString("notes");

                    int customerId = rs.getInt("customer_id");
                    String customerName = rs.getString("customer_name");
                    String customerDni = rs.getString("customer_dni");
                    String customerPhone = rs.getString("customer_phone");
                    String customerEmail = rs.getString("customer_email");

                    String vehicleText = rs.getString("vehicle_text");

                    return new RepairDetails(
                            repairId,
                            status != null ? status.trim() : "",
                            notes != null ? notes.trim() : "",
                            customerId,
                            customerName != null ? customerName.trim() : "",
                            customerDni != null ? customerDni.trim() : "",
                            customerPhone != null ? customerPhone.trim() : "",
                            customerEmail != null ? customerEmail.trim() : "",
                            vehicleText != null ? vehicleText.trim() : ""
                    );
                }
            }
        }

        return null;
    }

    public boolean startRepair(int repairId) throws Exception {

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_START_REPAIR)) {

            stmt.setInt(1, repairId);
            int updated = stmt.executeUpdate();
            return updated > 0;
        }
    }

    public boolean finishRepair(int repairId) throws Exception {

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_FINISH_REPAIR)) {

            stmt.setInt(1, repairId);
            int updated = stmt.executeUpdate();
            return updated > 0;
        }
    }

    public void createRepairOrder(int vehicleId, int customerId, int bossId, int mechanicId, String notes) throws Exception {

        String sql =
                "INSERT INTO repair_order " +
                "(vehicle_id, customer_id, created_by_boss_id, assigned_mechanic_id, status, notes) " +
                "VALUES (?, ?, ?, ?, 'ASSIGNED', ?)";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, vehicleId);
            stmt.setInt(2, customerId);
            stmt.setInt(3, bossId);
            stmt.setInt(4, mechanicId);
            stmt.setString(5, notes);

            stmt.executeUpdate();
        }
    }
}


