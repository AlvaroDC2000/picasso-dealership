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

    /**
     * Loads active customers in a compact id/name format for combo boxes.
     *
     * <p>This method is used by the Repairs module when a screen needs to populate
     * a customer selector. The returned items include a display-friendly text
     * (typically name plus DNI) mapped into {@link IdName} objects.</p>
     *
     * @return a list of customers formatted for combo box usage
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

    /**
     * Loads all active customers formatted for the Sales module customers table.
     *
     * <p>This method retrieves customer records used in the Sales -> Customers list screen
     * and maps each row into a {@link SalesCustomerRow} instance. Name fields are combined
     * into a single display value, and optional fields are normalized to avoid blank UI cells.</p>
     *
     * @return a list of customers formatted for the Sales customers table
     * @throws Exception if a database access error occurs
     */
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

    /**
     * Loads the detail information for a single customer by its identifier.
     *
     * <p>This method is used by customer detail and edit screens in the Sales module.
     * It fetches the customer fields from the database and maps them into a
     * {@link CustomerDetail} model. If the customer does not exist, the method returns {@code null}.</p>
     *
     * @param customerId the customer identifier to look up
     * @return a {@link CustomerDetail} instance if found, or {@code null} if no customer exists for the given id
     * @throws Exception if a database access error occurs
     */
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

    /**
     * Inserts a new customer record into the database.
     *
     * <p>This method is used by the Sales module "New Customer" flow.
     * The inserted customer is created as active by default.</p>
     *
     * @param dni the customer's DNI identifier
     * @param firstName the customer's first name
     * @param lastName the customer's last name
     * @param phone the customer's phone number
     * @param email the customer's email address
     * @throws Exception if a database access error occurs
     */
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

    /**
     * Updates an existing customer record with the provided values.
     *
     * <p>This method is used by customer edit screens in the Sales module.
     * It returns whether an update was actually applied (for example, false if the id does not exist).</p>
     *
     * @param customerId the customer identifier to update
     * @param firstName the updated first name
     * @param lastName the updated last name
     * @param phone the updated phone number
     * @param email the updated email address
     * @return {@code true} if the customer was updated, {@code false} otherwise
     * @throws Exception if a database access error occurs
     */
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
