package dealership.dao;

import dealership.model.RepairHistoryRow;
import dealership.util.DbConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for mechanic repair history.
 * <p>
 * This DAO provides read-only access to completed repair orders assigned
 * to a specific mechanic. The data returned is formatted for display
 * purposes in history tables.
 * </p>
 */
public class RepairHistoryDao {

    /**
     * SQL query used to retrieve finished repairs for a given mechanic.
     * <p>
     * It joins repair orders with vehicles, formats the vehicle description,
     * and returns only repairs with FINISHED status, ordered by completion date.
     * </p>
     */
    private static final String SQL_HISTORY_BY_MECHANIC =
            "SELECT " +
            "  ro.id AS repair_id, " +
            "  CONCAT(v.brand, ' ', v.model, ' ', YEAR(v.entry_date)) AS vehicle, " +
            "  ro.status AS status, " +
            "  DATE_FORMAT(ro.end_at, '%d/%m/%Y') AS end_date " +
            "FROM repair_order ro " +
            "JOIN vehicle v ON v.id = ro.vehicle_id " +
            "WHERE ro.assigned_mechanic_id = ? " +
            "  AND ro.status = 'FINISHED' " +
            "ORDER BY ro.end_at DESC, ro.id DESC";

    /**
     * Retrieves the repair history for a specific mechanic.
     * <p>
     * Only completed repairs are returned. Each result row is converted
     * into a {@link RepairHistoryRow} object with a user-friendly status
     * text and formatted completion date.
     * </p>
     *
     * @param mechanicId the identifier of the mechanic
     * @return a list of completed repairs for the given mechanic
     * @throws Exception if a database access error occurs
     */
    public List<RepairHistoryRow> findHistoryByMechanicId(int mechanicId) throws Exception {
        List<RepairHistoryRow> rows = new ArrayList<>();

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_HISTORY_BY_MECHANIC)) {

            ps.setInt(1, mechanicId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int repairId = rs.getInt("repair_id");
                    String vehicle = rs.getString("vehicle");
                    String status = rs.getString("status");
                    String date = rs.getString("end_date");

                    if (date == null) date = "-";

                    String statusText = "Completed";

                    rows.add(new RepairHistoryRow(repairId, vehicle, statusText, date));
                }
            }
        }

        return rows;
    }
}
