package dealership.dao;

import dealership.controllers.RegisterRepairController.IdName;
import dealership.util.DbConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class CustomerDao {

    private static final String SQL_FIND_ALL_CUSTOMERS =
            "SELECT c.id, CONCAT(c.first_name, ' ', c.last_name, ' (', c.dni, ')') AS customer_name " +
            "FROM customer c " +
            "ORDER BY c.id";

    public List<IdName> findAllCustomersForCombo() throws Exception {
        List<IdName> list = new ArrayList<>();

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_FIND_ALL_CUSTOMERS);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("customer_name");
                list.add(new IdName(id, name));
            }
        }

        return list;
    }
}
