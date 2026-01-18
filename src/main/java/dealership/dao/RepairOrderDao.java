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

/**
 * Data Access Object for repair orders.
 * <p>
 * This DAO centralizes database operations related to repair orders for both
 * mechanic and boss workflows: listing tasks, loading details, assigning/unassigning
 * mechanics, updating notes, and changing repair status (start/finish).
 * </p>
 */
public class RepairOrderDao {

    /**
     * SQL query to retrieve repair tasks assigned to a specific mechanic.
     */
    private static final String SQL_FIND_TASKS_BY_MECHANIC =
            "SELECT " +
            "   ro.id AS repair_id, " +
            "   CONCAT(v.brand, ' ', v.model) AS vehicle, " +
            "   ro.status AS status " +
            "FROM repair_order ro " +
            "JOIN vehicle v ON v.id = ro.vehicle_id " +
            "WHERE ro.assigned_mechanic_id = ? " +
            "ORDER BY ro.id ASC";

    /**
     * SQL query to retrieve repairs created by a specific boss.
     */
    private static final String SQL_FIND_REPAIRS_BY_BOSS =
            "SELECT " +
            "   ro.id AS repair_id, " +
            "   CONCAT(v.brand, ' ', v.model) AS vehicle, " +
            "   ro.status AS status " +
            "FROM repair_order ro " +
            "JOIN vehicle v ON v.id = ro.vehicle_id " +
            "WHERE ro.created_by_boss_id = ? " +
            "ORDER BY ro.id ASC";

    /**
     * SQL query to retrieve repair edit details for the boss edit screen.
     * <p>
     * It verifies ownership by boss ID and includes optional mechanic data.
     * </p>
     */
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

    /**
     * SQL query to assign a mechanic, set status to ASSIGNED, and update notes.
     * <p>
     * It only applies when the current status is PENDING or ASSIGNED.
     * </p>
     */
    private static final String SQL_ASSIGN_MECHANIC_AND_UPDATE_NOTES =
            "UPDATE repair_order " +
            "SET assigned_mechanic_id = ?, " +
            "    status = 'ASSIGNED', " +
            "    notes = ? " +
            "WHERE id = ? " +
            "  AND created_by_boss_id = ? " +
            "  AND UPPER(TRIM(status)) IN ('PENDING','ASSIGNED')";

    /**
     * SQL query to unassign the mechanic, set status to PENDING, and update notes.
     * <p>
     * It only applies when the current status is PENDING or ASSIGNED.
     * </p>
     */
    private static final String SQL_UNASSIGN_MECHANIC_AND_UPDATE_NOTES =
            "UPDATE repair_order " +
            "SET assigned_mechanic_id = NULL, " +
            "    status = 'PENDING', " +
            "    notes = ? " +
            "WHERE id = ? " +
            "  AND created_by_boss_id = ? " +
            "  AND UPPER(TRIM(status)) IN ('PENDING','ASSIGNED')";

    /**
     * SQL query to retrieve full repair details including customer and vehicle data.
     */
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

    /**
     * SQL query to start a repair (ASSIGNED -> IN_PROGRESS).
     * <p>
     * The start timestamp is set only if it was not already set.
     * </p>
     */
    private static final String SQL_START_REPAIR =
            "UPDATE repair_order " +
            "SET status = 'IN_PROGRESS', start_at = COALESCE(start_at, NOW()) " +
            "WHERE id = ? AND UPPER(TRIM(status)) = 'ASSIGNED'";

    /**
     * SQL query to finish a repair (IN_PROGRESS -> FINISHED).
     */
    private static final String SQL_FINISH_REPAIR =
            "UPDATE repair_order " +
            "SET status = 'FINISHED', end_at = NOW() " +
            "WHERE id = ? AND UPPER(TRIM(status)) = 'IN_PROGRESS'";

    /**
     * Retrieves the list of tasks assigned to a given mechanic.
     *
     * @param mechanicUserId the mechanic user ID
     * @return list of repair tasks assigned to the mechanic
     * @throws Exception if a database access error occurs
     */
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

    /**
     * Retrieves the list of repairs created by the given boss.
     *
     * @param bossId the boss user ID
     * @return list of repairs created by the boss
     * @throws Exception if a database access error occurs
     */
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

    /**
     * Retrieves repair edit details for the boss repair edit screen.
     * <p>
     * The query also checks that the repair belongs to the boss (created_by_boss_id),
     * so it doubles as a permission check.
     * </p>
     *
     * @param repairId the repair order ID
     * @param bossId the boss user ID
     * @return details object for editing, or null if not found / no permissions
     * @throws Exception if a database access error occurs
     */
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

    /**
     * Assigns a mechanic to a repair and updates notes (boss flow).
     * <p>
     * This only updates repairs owned by the boss and with an editable status
     * (PENDING or ASSIGNED). The status is forced to ASSIGNED when saving.
     * </p>
     *
     * @param repairId the repair order ID
     * @param bossId the boss user ID
     * @param mechanicId the mechanic user ID to assign
     * @param notes the notes to store
     * @return true if at least one row was updated, false otherwise
     * @throws Exception if a database access error occurs
     */
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

    /**
     * Unassigns the mechanic from a repair and updates notes (boss flow).
     * <p>
     * This resets the status to PENDING and clears the assigned mechanic.
     * The operation only applies when the repair belongs to the boss and has
     * an editable status (PENDING or ASSIGNED).
     * </p>
     *
     * @param repairId the repair order ID
     * @param bossId the boss user ID
     * @param notes the notes to store
     * @return true if at least one row was updated, false otherwise
     * @throws Exception if a database access error occurs
     */
    public boolean unassignMechanicAndUpdateNotes(int repairId, int bossId, String notes) throws Exception {

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_UNASSIGN_MECHANIC_AND_UPDATE_NOTES)) {

            stmt.setString(1, notes);
            stmt.setInt(2, repairId);
            stmt.setInt(3, bossId);

            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Retrieves full repair details by repair ID (mechanic flow).
     * <p>
     * This includes status, notes, customer information and the vehicle text
     * used in the details and customer screens.
     * </p>
     *
     * @param repairId the repair order ID
     * @return the {@link RepairDetails} object, or null if not found
     * @throws Exception if a database access error occurs
     */
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

    /**
     * Starts a repair by changing its status from ASSIGNED to IN_PROGRESS.
     *
     * @param repairId the repair order ID
     * @return true if the status was updated, false otherwise
     * @throws Exception if a database access error occurs
     */
    public boolean startRepair(int repairId) throws Exception {

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_START_REPAIR)) {

            stmt.setInt(1, repairId);
            int updated = stmt.executeUpdate();
            return updated > 0;
        }
    }

    /**
     * Finishes a repair by changing its status from IN_PROGRESS to FINISHED.
     *
     * @param repairId the repair order ID
     * @return true if the status was updated, false otherwise
     * @throws Exception if a database access error occurs
     */
    public boolean finishRepair(int repairId) throws Exception {

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_FINISH_REPAIR)) {

            stmt.setInt(1, repairId);
            int updated = stmt.executeUpdate();
            return updated > 0;
        }
    }

    /**
     * Creates a new repair order (boss flow).
     * <p>
     * The repair is created with status ASSIGNED and immediately linked to
     * the selected mechanic. Notes are mandatory at controller level.
     * </p>
     *
     * @param vehicleId the vehicle ID
     * @param customerId the customer ID
     * @param bossId the boss user ID (creator)
     * @param mechanicId the mechanic user ID to assign
     * @param notes initial notes for the repair
     * @throws Exception if a database access error occurs
     */
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
