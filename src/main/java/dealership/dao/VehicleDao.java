package dealership.dao;

import dealership.controllers.RegisterRepairController.IdName;
import dealership.util.DbConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for vehicle-related queries.
 * <p>
 * This DAO is used to retrieve vehicle data from the database in a simplified
 * {@link IdName} format, mainly for use in combo boxes and selection controls
 * when registering new repairs.
 * </p>
 */
public class VehicleDao {

    /**
     * SQL query used to retrieve all vehicles.
     * <p>
     * It builds a display name by concatenating brand and model,
     * ordered by vehicle ID.
     * </p>
     */
    private static final String SQL_FIND_ALL_VEHICLES =
            "SELECT v.id, CONCAT(v.brand, ' ', v.model) AS vehicle_name " +
            "FROM vehicle v " +
            "ORDER BY v.id";

    /**
     * Retrieves all vehicles from the database for use in combo boxes.
     * <p>
     * Each vehicle is returned as an {@link IdName} object containing
     * the vehicle ID and a human-readable name (brand + model).
     * </p>
     *
     * @return a list of vehicles formatted as {@link IdName} objects
     * @throws Exception if a database access error occurs
     */
    public List<IdName> findAllVehiclesForCombo() throws Exception {
        List<IdName> list = new ArrayList<>();

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_FIND_ALL_VEHICLES);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("vehicle_name");
                list.add(new IdName(id, name));
            }
        }

        return list;
    }
}
