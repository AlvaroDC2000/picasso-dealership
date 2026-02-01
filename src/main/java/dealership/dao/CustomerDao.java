package dealership.dao;

import dealership.controllers.RegisterRepairController.IdName;
import dealership.model.CustomerDetail;
import dealership.model.SalesCustomerRow;
import dealership.util.DbConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for customer-related queries.
 *
 * CRUD for Sales module:
 * - Create: insertCustomer(...)
 * - Read: findAllCustomersForSales(...), findCustomerDetailById(...)
 * - Update: updateCustomer(...)
 * - Delete (soft): deleteCustomerById(...) => sets active = 0
 *
 * Repairs module combos:
 * - findAllCustomersForCombo(...)
 */
public class CustomerDao {

    private static final String SQL_FIND_ALL_CUSTOMERS =
            "SELECT c.id, CONCAT(c.first_name, ' ', c.last_name, ' (', c.dni, ')') AS customer_name " +
            "FROM customer c " +
            "WHERE c.active = 1 " +
            "ORDER BY c.id";

    private static final String SQL_FIND_ALL_CUSTOMERS_FOR_SALES =
            "SELECT c.id, c.first_name, c.last_name, c.email, c.phone " +
            "FROM customer c " +
            "WHERE c.active = 1 " +
            "ORDER BY c.last_name ASC, c.first_name ASC, c.id ASC";

    private static final String SQL_FIND_CUSTOMER_DETAIL_BY_ID =
            "SELECT c.id, c.dni, c.first_name, c.last_name, c.phone, c.email, c.active " +
            "FROM customer c " +
            "WHERE c.id = ?";

    private static final String SQL_INSERT_CUSTOMER =
            "INSERT INTO customer (dni, first_name, last_name, phone, email, active) " +
            "VALUES (?, ?, ?, ?, ?, 1)";

    private static final String SQL_UPDATE_CUSTOMER =
            "UPDATE customer " +
            "SET first_name = ?, last_name = ?, phone = ?, email = ? " +
            "WHERE id = ?";

    // Soft delete
    private static final String SQL_SOFT_DELETE_CUSTOMER_BY_ID =
            "UPDATE customer SET active = 0 WHERE id = ?";

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

    public List<SalesCustomerRow> findAllCustomersForSales() throws Exception {
        List<SalesCustomerRow> list = new ArrayList<>();

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_FIND_ALL_CUSTOMERS_FOR_SALES);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                String email = rs.getString("email");
                String phone = rs.getString("phone");

                String fullName = buildFullName(firstName, lastName);
                list.add(new SalesCustomerRow(
                        id,
                        fullName,
                        safeText(email),
                        safeText(phone)
                ));
            }
        }

        return list;
    }

    public CustomerDetail findCustomerDetailById(int customerId) throws Exception {
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_FIND_CUSTOMER_DETAIL_BY_ID)) {

            ps.setInt(1, customerId);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }

                int id = rs.getInt("id");
                String dni = rs.getString("dni");
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                String phone = rs.getString("phone");
                String email = rs.getString("email");

                return new CustomerDetail(id, dni, firstName, lastName, phone, email);
            }
        }
    }

    public void insertCustomer(String dni, String firstName, String lastName, String phone, String email) throws Exception {
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_INSERT_CUSTOMER)) {

            ps.setString(1, dni);
            ps.setString(2, firstName);
            ps.setString(3, lastName);
            ps.setString(4, phone);
            ps.setString(5, email);

            ps.executeUpdate();
        }
    }

    public boolean updateCustomer(int customerId, String firstName, String lastName, String phone, String email) throws Exception {
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_UPDATE_CUSTOMER)) {

            ps.setString(1, firstName);
            ps.setString(2, lastName);
            ps.setString(3, phone);
            ps.setString(4, email);
            ps.setInt(5, customerId);

            return ps.executeUpdate() > 0;
        }
    }

    /**
     * "Delete" customer (soft delete).
     * It sets active = 0 to avoid FK constraint violations (repair_order, sales, etc.).
     *
     * @return true if updated, false otherwise
     */
    public boolean deleteCustomerById(int customerId) throws Exception {
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_SOFT_DELETE_CUSTOMER_BY_ID)) {

            ps.setInt(1, customerId);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Builds a readable full name from first and last name.
     * <p>
     * It trims each part and returns "-" if both parts are empty.
     * </p>
     *
     * @param firstName raw first name (can be null)
     * @param lastName raw last name (can be null)
     * @return a clean full name or "-" if empty
     */
    private String buildFullName(String firstName, String lastName) {
        String fn = (firstName != null) ? firstName.trim() : "";
        String ln = (lastName != null) ? lastName.trim() : "";
        String full = (fn + " " + ln).trim();
        return full.isBlank() ? "-" : full;
    }

    /**
     * Returns a safe text value for table rendering.
     * <p>
     * If the database value is null or blank, it returns "-".
     * </p>
     *
     * @param value raw value from database
     * @return sanitized string for UI usage
     */
    private String safeText(String value) {
        return (value == null || value.isBlank()) ? "-" : value;
    }
}

