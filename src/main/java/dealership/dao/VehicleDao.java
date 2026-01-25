package dealership.dao;

import dealership.controllers.RegisterRepairController.IdName;
import dealership.model.SalesVehicleRow;
import dealership.model.VehicleDetail;
import dealership.util.DbConnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for vehicle-related queries.
 * <p>
 * This DAO is used to retrieve vehicle data from the database.
 * </p>
 */
public class VehicleDao {

    /**
     * SQL query used to retrieve all vehicles for combos (repairs module).
     */
    private static final String SQL_FIND_ALL_VEHICLES =
            "SELECT v.id, CONCAT(v.brand, ' ', v.model) AS vehicle_name " +
            "FROM vehicle v " +
            "ORDER BY v.id";

    /**
     * SQL query used to retrieve vehicles for Sales -> Vehicles list.
     */
    private static final String SQL_FIND_ALL_VEHICLES_FOR_SALES =
            "SELECT v.id, v.plate, v.brand, v.model, v.year, v.color, v.entry_date " +
            "FROM vehicle v " +
            "ORDER BY v.entry_date DESC, v.id DESC";

    /**
     * SQL query used to retrieve vehicle detail for Sales -> Vehicle detail view.
     */
    private static final String SQL_FIND_VEHICLE_DETAIL_BY_ID =
            "SELECT v.id, v.plate, v.brand, v.model, v.year, v.color, v.mileage, v.notes, " +
            "       v.fuel, v.transmission, v.doors, v.entry_date, " +
            "       vc.name AS type_name " +
            "FROM vehicle v " +
            "LEFT JOIN vehicle_category vc ON v.category_id = vc.id " +
            "WHERE v.id = ?";

    /**
     * Retrieves all vehicles from the database for use in combo boxes.
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

    /**
     * Retrieves all vehicles for the Sales -> Vehicles table.
     * This returns a lightweight row model ready to be used in a TableView.
     *
     * @return list of sales vehicle rows
     * @throws Exception if a database access error occurs
     */
    public List<SalesVehicleRow> findAllVehiclesForSales() throws Exception {
        List<SalesVehicleRow> list = new ArrayList<>();

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_FIND_ALL_VEHICLES_FOR_SALES);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String plate = rs.getString("plate");

                String brand = rs.getString("brand");
                String model = rs.getString("model");
                Integer year = (Integer) rs.getObject("year");
                String color = rs.getString("color");

                Date entryDateSql = rs.getDate("entry_date");
                java.time.LocalDate entryDate = (entryDateSql != null) ? entryDateSql.toLocalDate() : null;

                String vehicleText = buildVehicleText(brand, model, color, year);
                list.add(new SalesVehicleRow(id, plate, vehicleText, entryDate));
            }
        }

        return list;
    }

    private String buildVehicleText(String brand, String model, String color, Integer year) {
        String brandText = (brand != null) ? brand : "";
        String modelText = (model != null) ? model : "";
        String colorText = (color != null) ? color : "";
        String yearText = (year != null) ? String.valueOf(year) : "";

        // Format: "Brand Model Color Year"
        // Example: "Ford Fiesta Black 2017"
        String text = (brandText + " " + modelText + " " + colorText + " " + yearText).trim();

        return text.isBlank() ? "-" : text;
    }

    /**
     * Retrieves full vehicle information by its id.
     *
     * @param vehicleId vehicle id
     * @return VehicleDetail object or null if not found
     * @throws Exception if a database access error occurs
     */
    public VehicleDetail findVehicleDetailById(int vehicleId) throws Exception {
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_FIND_VEHICLE_DETAIL_BY_ID)) {

            ps.setInt(1, vehicleId);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }

                int id = rs.getInt("id");
                String plate = rs.getString("plate");
                String brand = rs.getString("brand");
                String model = rs.getString("model");
                Integer year = (Integer) rs.getObject("year");
                String color = rs.getString("color");
                Integer mileage = (Integer) rs.getObject("mileage");
                String notes = rs.getString("notes");

                String type = rs.getString("type_name");
                String fuel = rs.getString("fuel");
                String transmission = rs.getString("transmission");
                Integer doors = (Integer) rs.getObject("doors");

                Date entryDateSql = rs.getDate("entry_date");
                java.time.LocalDate entryDate = (entryDateSql != null) ? entryDateSql.toLocalDate() : null;

                return new VehicleDetail(
                        id, plate, brand, model, year, color, mileage, notes,
                        type, fuel, transmission, doors, entryDate
                );
            }
        }
    }
}


