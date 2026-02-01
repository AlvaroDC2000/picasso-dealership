package dealership.dao;

import dealership.model.SaleDetail;
import dealership.model.SalesSaleRow;
import dealership.util.DbConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for sales-related queries.
 * <p>
 * This DAO is used by the Sales module to:
 * - List all sales in the Sales -> Sales table.
 * - Load sale detail for the Sales -> Sale detail screen.
 * - Create a sale from an accepted proposal.
 * </p>
 */
public class SaleDao {

    /**
     * SQL query used to retrieve all sales for the Sales -> Sales list table.
     */
    private static final String SQL_FIND_ALL_SALES =
            "SELECT s.id, " +
            "       CONCAT(v.brand, ' ', v.model, ' ', v.color, ' ', v.year) AS vehicle_text, " +
            "       CONCAT(c.first_name, ' ', c.last_name) AS customer_name, " +
            "       s.price " +
            "FROM sale s " +
            "JOIN customer c ON s.customer_id = c.id " +
            "JOIN vehicle v ON s.vehicle_id = v.id " +
            "ORDER BY s.id DESC";

    /**
     * SQL query used to retrieve full sale information by id.
     */
    private static final String SQL_FIND_SALE_DETAIL =
            "SELECT s.id, s.price, s.sale_date, s.notes, " +
            "       CONCAT(c.first_name, ' ', c.last_name) AS customer_name, " +
            "       CONCAT(v.brand, ' ', v.model, ' ', v.color, ' ', v.year) AS vehicle_text " +
            "FROM sale s " +
            "JOIN customer c ON s.customer_id = c.id " +
            "JOIN vehicle v ON s.vehicle_id = v.id " +
            "WHERE s.id = ?";

    /**
     * SQL insert statement that creates a sale from an existing proposal.
     * It copies customer, vehicle, seller user, dealership, price and notes from sale_proposal.
     */
    private static final String SQL_CREATE_SALE_FROM_PROPOSAL =
            "INSERT INTO sale (proposal_id, customer_id, vehicle_id, seller_user_id, dealership_id, price, sale_date, notes) " +
            "SELECT sp.id, sp.customer_id, sp.vehicle_id, sp.seller_user_id, sp.dealership_id, sp.price, ?, sp.notes " +
            "FROM sale_proposal sp " +
            "WHERE sp.id = ?";

    /**
     * Loads all sales for the Sales module table.
     * Each row includes a formatted code (00001), vehicle, customer and price.
     *
     * @return list of sales formatted for the Sales -> Sales table
     * @throws Exception if a database access error occurs
     */
    public List<SalesSaleRow> findAllSalesForSales() throws Exception {
        List<SalesSaleRow> list = new ArrayList<>();

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_FIND_ALL_SALES);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String code = String.format("%05d", id);
                String vehicle = rs.getString("vehicle_text");
                String customer = rs.getString("customer_name");
                BigDecimal price = rs.getBigDecimal("price");

                list.add(new SalesSaleRow(
                        id,
                        code,
                        safeText(vehicle),
                        safeText(customer),
                        formatPrice(price)
                ));
            }
        }

        return list;
    }

    /**
     * Loads full details for a single sale by its id.
     *
     * @param saleId sale id
     * @return SaleDetail instance or null if the sale does not exist
     * @throws Exception if a database access error occurs
     */
    public SaleDetail findSaleDetailById(int saleId) throws Exception {
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_FIND_SALE_DETAIL)) {

            ps.setInt(1, saleId);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;

                int id = rs.getInt("id");
                BigDecimal price = rs.getBigDecimal("price");
                Date saleDateSql = rs.getDate("sale_date");
                LocalDate saleDate = (saleDateSql != null) ? saleDateSql.toLocalDate() : null;
                String notes = rs.getString("notes");

                String customerName = rs.getString("customer_name");
                String vehicleText = rs.getString("vehicle_text");

                return new SaleDetail(
                        id,
                        safeText(customerName),
                        safeText(vehicleText),
                        price,
                        saleDate,
                        notes
                );
            }
        }
    }

    /**
     * Creates a sale from a proposal (proposal_id must be unique in sale).
     *
     * @param proposalId proposal id to be converted into a sale
     * @param saleDate date to store as sale_date
     * @throws Exception if a database access error occurs
     */
    public void createSaleFromProposal(int proposalId, LocalDate saleDate) throws Exception {
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_CREATE_SALE_FROM_PROPOSAL)) {

            ps.setDate(1, Date.valueOf(saleDate));
            ps.setInt(2, proposalId);

            ps.executeUpdate();
        }
    }

    /**
     * Returns a safe text value to avoid blank/null UI rendering.
     *
     * @param v raw value from database
     * @return "-" if null/blank, otherwise the original value
     */
    private String safeText(String v) {
        return (v == null || v.isBlank()) ? "-" : v;
    }

    /**
     * Formats a BigDecimal price for UI display.
     * <p>
     * It removes trailing zeros (ex: 12000.00 -> 12000) and returns "-" if null.
     * </p>
     *
     * @param price price value from database
     * @return formatted price string for tables
     */
    private String formatPrice(BigDecimal price) {
        if (price == null) return "-";
        return price.stripTrailingZeros().toPlainString();
    }
}
