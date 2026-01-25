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
 * <p>
 * This DAO is used across modules:
 * - Repairs module: retrieve customers as IdName for combo boxes.
 * - Sales module: list customers, view detail, and create new customers.
 * </p>
 */
public class CustomerDao {

    /**
     * SQL query used to retrieve all customers for combo boxes (repairs module).
     */
    private static final String SQL_FIND_ALL_CUSTOMERS =
            "SELECT c.id, CONCAT(c.first_name, ' ', c.last_name, ' (', c.dni, ')') AS customer_name " +
            "FROM customer c " +
            "ORDER BY c.id";

    /**
     * SQL query used to retrieve customers for Sales -> Customers list.
     */
    private static final String SQL_FIND_ALL_CUSTOMERS_FOR_SALES =
            "SELECT c.id, c.first_name, c.last_name, c.email, c.phone " +
            "FROM customer c " +
            "ORDER BY c.last_name ASC, c.first_name ASC, c.id ASC";

    /**
     * SQL query used to retrieve customer detail by id for Sales -> Customer detail view.
     */
    private static final String SQL_FIND_CUSTOMER_DETAIL_BY_ID =
            "SELECT c.id, c.dni, c.first_name, c.last_name, c.phone, c.email " +
            "FROM customer c " +
            "WHERE c.id = ?";

    /**
     * SQL query used to insert a new customer (Sales -> New customer).
     */
    private static final String SQL_INSERT_CUSTOMER =
            "INSERT INTO customer (dni, first_name, last_name, phone, email) " +
            "VALUES (?, ?, ?, ?, ?)";

    /**
     * Retrieves all customers from the database for use in combo boxes.
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

    /**
     * Retrieves all customers for the Sales -> Customers table.
     *
     * @return list of customer rows for Sales
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
     * Retrieves full customer information by its id.
     *
     * @param customerId customer id
     * @return CustomerDetail object or null if not found
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
     * Inserts a new customer into the database.
     *
     * @param dni customer DNI (NOT NULL UNIQUE)
     * @param firstName first name
     * @param lastName last name
     * @param phone phone number
     * @param email email address
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
     * Builds a full name string using first and last name.
     * <p>
     * This method trims values and ensures a non-empty display text is returned.
     * </p>
     *
     * @param firstName the customer first name
     * @param lastName the customer last name
     * @return a formatted full name, or "-" if empty
     */
    private String buildFullName(String firstName, String lastName) {
        String fn = (firstName != null) ? firstName.trim() : "";
        String ln = (lastName != null) ? lastName.trim() : "";
        String full = (fn + " " + ln).trim();
        return full.isBlank() ? "-" : full;
    }

    /**
     * Returns a safe text value for UI display.
     * <p>
     * If the provided value is null or blank, a dash ("-") is returned.
     * </p>
     *
     * @param value the original text value
     * @return a safe text string
     */
    private String safeText(String value) {
        return (value == null || value.isBlank()) ? "-" : value;
    }
}
