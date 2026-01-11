package dealership.dao;

import dealership.controllers.RegisterRepairController.IdName;
import dealership.util.DbConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class VehicleDao {

    private static final String SQL_FIND_ALL_VEHICLES =
            "SELECT v.id, CONCAT(v.brand, ' ', v.model) AS vehicle_name " +
            "FROM vehicle v " +
            "ORDER BY v.id";

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
