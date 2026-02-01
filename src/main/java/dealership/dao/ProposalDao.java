package dealership.dao;

import dealership.model.ProposalDetail;
import dealership.model.SalesProposalRow;
import dealership.util.DbConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProposalDao {

    private static final String SQL_FIND_ALL_PROPOSALS =
            "SELECT sp.id, " +
            "       CONCAT(v.brand, ' ', v.model, ' ', v.color, ' ', v.year) AS vehicle_text, " +
            "       CONCAT(c.first_name, ' ', c.last_name) AS customer_name, " +
            "       sp.price " +
            "FROM sale_proposal sp " +
            "JOIN customer c ON sp.customer_id = c.id " +
            "JOIN vehicle v ON sp.vehicle_id = v.id " +
            "ORDER BY sp.id DESC";

    private static final String SQL_FIND_PROPOSAL_DETAIL =
            "SELECT sp.id, sp.customer_id, sp.vehicle_id, sp.price, sp.notes, sp.status, " +
            "       CONCAT(c.first_name, ' ', c.last_name) AS customer_name, " +
            "       CONCAT(v.brand, ' ', v.model, ' ', v.color, ' ', v.year) AS vehicle_text " +
            "FROM sale_proposal sp " +
            "JOIN customer c ON sp.customer_id = c.id " +
            "JOIN vehicle v ON sp.vehicle_id = v.id " +
            "WHERE sp.id = ?";

    private static final String SQL_INSERT_PROPOSAL =
            "INSERT INTO sale_proposal (customer_id, vehicle_id, seller_user_id, dealership_id, price, notes, status) " +
            "VALUES (?, ?, ?, ?, ?, ?, 'ACTIVE')";

    private static final String SQL_UPDATE_PROPOSAL =
            "UPDATE sale_proposal " +
            "SET price = ?, notes = ?, status = ? " +
            "WHERE id = ?";

    private static final String SQL_DELETE_PROPOSAL =
            "DELETE FROM sale_proposal WHERE id = ?";

    private static final String SQL_SET_STATUS =
            "UPDATE sale_proposal SET status = ? WHERE id = ?";

    // IMPORTANT: check if proposal is referenced by sale
    private static final String SQL_EXISTS_SALE_BY_PROPOSAL =
            "SELECT 1 FROM sale s WHERE s.proposal_id = ? LIMIT 1";

    public List<SalesProposalRow> findAllProposalsForSales() throws Exception {
        List<SalesProposalRow> list = new ArrayList<>();

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_FIND_ALL_PROPOSALS);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String code = String.format("%05d", id);
                String vehicle = rs.getString("vehicle_text");
                String customer = rs.getString("customer_name");
                BigDecimal price = rs.getBigDecimal("price");

                list.add(new SalesProposalRow(
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

    public ProposalDetail findProposalDetailById(int proposalId) throws Exception {
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_FIND_PROPOSAL_DETAIL)) {

            ps.setInt(1, proposalId);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;

                int id = rs.getInt("id");
                int customerId = rs.getInt("customer_id");
                int vehicleId = rs.getInt("vehicle_id");

                BigDecimal price = rs.getBigDecimal("price");
                String notes = rs.getString("notes");
                String status = rs.getString("status");

                String customerName = rs.getString("customer_name");
                String vehicleText = rs.getString("vehicle_text");

                return new ProposalDetail(
                        id, customerId, vehicleId,
                        safeText(customerName),
                        safeText(vehicleText),
                        price,
                        notes,
                        safeText(status)
                );
            }
        }
    }

    public void insertProposal(int customerId, int vehicleId, int sellerUserId, int dealershipId,
                               BigDecimal price, String notes) throws Exception {
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_INSERT_PROPOSAL)) {

            ps.setInt(1, customerId);
            ps.setInt(2, vehicleId);
            ps.setInt(3, sellerUserId);
            ps.setInt(4, dealershipId);
            ps.setBigDecimal(5, price);
            ps.setString(6, emptyToNull(notes));

            ps.executeUpdate();
        }
    }

    public void updateProposal(int proposalId, BigDecimal price, String notes, String status) throws Exception {
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_UPDATE_PROPOSAL)) {

            ps.setBigDecimal(1, price);
            ps.setString(2, emptyToNull(notes));
            ps.setString(3, status);
            ps.setInt(4, proposalId);

            ps.executeUpdate();
        }
    }

    public void setProposalStatus(int proposalId, String status) throws Exception {
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_SET_STATUS)) {

            ps.setString(1, status);
            ps.setInt(2, proposalId);

            ps.executeUpdate();
        }
    }

    public boolean isProposalAlreadySold(int proposalId) throws Exception {
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_EXISTS_SALE_BY_PROPOSAL)) {

            ps.setInt(1, proposalId);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    /**
     * Deletes a proposal only if it is NOT referenced by a sale.
     *
     * @return true if deleted, false if it cannot be deleted because it is already sold
     */
    public boolean deleteProposalById(int proposalId) throws Exception {

        if (isProposalAlreadySold(proposalId)) {
            return false; // avoid FK explosion, UI should show message
        }

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_DELETE_PROPOSAL)) {

            ps.setInt(1, proposalId);
            return ps.executeUpdate() > 0;
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
     * Converts an optional text into null if it is empty.
     * <p>
     * This is used for notes fields so the database stores NULL instead of "".
     * </p>
     *
     * @param v raw string (can be null)
     * @return null if empty/blank, otherwise trimmed value
     */
    private String emptyToNull(String v) {
        if (v == null) return null;
        String t = v.trim();
        return t.isEmpty() ? null : t;
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

