package dealership.dao;

import dealership.controllers.RegisterRepairController.IdName;
import dealership.util.DbConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for customer-related queries.
 * <p>
 * This DAO is mainly used to retrieve customer data in a simplified
 * {@link IdName} format, suitable for combo boxes and selection dialogs
 * in the UI layer.
 * </p>
 */
public class CustomerDao {

    /**
     * SQL query used to retrieve all customers.
     * <p>
     * It concatenates first name, last name and DNI into a single display
     * string, ordered by customer ID.
     * </p>
     */
    private static final String SQL_FIND_ALL_CUSTOMERS =
            "SELECT c.id, CONCAT(c.first_name, ' ', c.last_name, ' (', c.dni, ')') AS customer_name " +
            "FROM customer c " +
            "ORDER BY c.id";

    /**
     * Retrieves all customers from the database for use in combo boxes.
     * <p>
     * Each customer is returned as an {@link IdName} object, where the ID
     * represents the customer identifier and the name is a formatted
     * string combining name, surname and DNI.
     * </p>
     *
     * @return a list of customers formatted as {@link IdName} objects
     * @throws Exception if a database access error occurs
     */
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
