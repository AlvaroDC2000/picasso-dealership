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
 *
 * <p>
 * It maps raw database rows into UI-friendly models such as {@link SalesSaleRow}
 * for tables and {@link SaleDetail} for detail screens.
 * </p>
 */
public class SaleDao {

    /**
     * SQL query used to retrieve all sales for the Sales -> Sales list table.
     * We include sale_date and sort by date desc (and id desc as tie-breaker).
     */
    private static final String SQL_FIND_ALL_SALES =
            "SELECT s.id, " +
            "       CONCAT(v.brand, ' ', v.model, ' ', v.color, ' ', v.year) AS vehicle_text, " +
            "       CONCAT(c.first_name, ' ', c.last_name) AS customer_name, " +
            "       s.price, " +
            "       s.sale_date " +
            "FROM sale s " +
            "JOIN customer c ON s.customer_id = c.id " +
            "JOIN vehicle v ON s.vehicle_id = v.id " +
            "ORDER BY s.sale_date DESC, s.id DESC";

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
     *
     * <p>This method is used by the Sales -> Sales list screen. It retrieves sales
     * joined with customer and vehicle information, then maps each row into a
     * {@link SalesSaleRow} instance suitable for JavaFX table rendering.</p>
     *
     * <p>The sale code is derived from the sale ID and padded for display.
     * The sale date is kept as a {@link LocalDate} to support correct sorting in the UI.</p>
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

                Date saleDateSql = rs.getDate("sale_date");
                LocalDate saleDate = (saleDateSql != null) ? saleDateSql.toLocalDate() : null;

                list.add(new SalesSaleRow(
                        id,
                        code,
                        safeText(vehicle),
                        safeText(customer),
                        formatPrice(price),
                        saleDate
                ));
            }
        }

        return list;
    }

    /**
     * Loads full details for a single sale by its id.
     *
     * <p>This method is used by the Sales -> Sale detail screen. It retrieves
     * the core sale data along with display-friendly customer and vehicle text,
     * then maps the result into a {@link SaleDetail} model.</p>
     *
     * @param saleId the sale identifier to look up
     * @return a {@link SaleDetail} instance if found, or {@code null} if no matching sale exists
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
     * <p>This method is typically called after a proposal has been accepted.
     * It inserts a new row into the {@code sale} table by copying fields
     * from {@code sale_proposal} and applying the provided sale date.</p>
     *
     * @param proposalId the proposal identifier to convert into a sale
     * @param saleDate the date to store as the sale date
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
     * <p>This helper normalizes database strings so UI tables and labels do not
     * display empty values. It mirrors the formatting strategy used across
     * the Sales DAOs.</p>
     *
     * @param v raw value from database
     * @return "-" if null/blank, otherwise the original value
     */
    private String safeText(String v) {
        return (v == null || v.isBlank()) ? "-" : v;
    }

    /**
     * Formats a {@link BigDecimal} price for UI display.
     *
     * <p>This helper produces a plain string representation without trailing zeros,
     * matching the formatting expected by the Sales tables.</p>
     *
     * @param price price value from database
     * @return formatted price string, or "-" if the value is null
     */
    private String formatPrice(BigDecimal price) {
        if (price == null) return "-";
        return price.stripTrailingZeros().toPlainString();
    }
}

