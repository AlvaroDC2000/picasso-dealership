package dealership.dao;

import dealership.model.ProposalDetail;
import dealership.model.SalesProposalRow;
import dealership.util.DbConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) for sales proposals in the dealership application.
 *
 * <p>This class encapsulates all database operations related to the {@code sale_proposal}
 * table, including listing proposals for the Sales module, loading a proposal detail,
 * inserting new proposals, updating existing ones, and enforcing safe deletion rules.</p>
 *
 * <p>It also provides small helper methods to normalize database values into
 * UI-friendly strings (for example, avoiding blank values and formatting prices).</p>
 */
public class ProposalDao {

    private static final String SQL_FIND_ALL_PROPOSALS =
            "SELECT sp.id, " +
            "       CONCAT(v.brand, ' ', v.model, ' ', v.color, ' ', v.year) AS vehicle_text, " +
            "       CONCAT(c.first_name, ' ', c.last_name) AS customer_name, " +
            "       sp.price, " +
            "       sp.status " +
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

    private static final String SQL_EXISTS_SALE_BY_PROPOSAL =
            "SELECT 1 FROM sale s WHERE s.proposal_id = ? LIMIT 1";

    /**
     * Retrieves all sales proposals in a table-friendly format for the Sales module.
     *
     * <p>This method executes a query that joins proposals with customer and vehicle data,
     * then maps each database row into a {@link SalesProposalRow} instance suitable for
     * JavaFX {@code TableView} rendering.</p>
     *
     * <p>The proposal code is derived from the proposal ID and padded for display.
     * Some values are normalized to avoid null/blank rendering in the UI.</p>
     *
     * @return a list of proposal rows for display in the Sales proposals list screen
     * @throws Exception if a database access error occurs
     */
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
                String status = rs.getString("status");

                list.add(new SalesProposalRow(
                        id,
                        code,
                        safeText(vehicle),
                        safeText(customer),
                        formatPrice(price),
                        safeText(status)
                ));
            }
        }

        return list;
    }

    /**
     * Loads a single proposal in detail by its identifier.
     *
     * <p>This method is used by the proposal detail screen. It fetches the proposal data
     * together with the associated customer and vehicle display text, and maps it into
     * a {@link ProposalDetail} model.</p>
     *
     * @param proposalId the proposal identifier to look up
     * @return a {@link ProposalDetail} instance if found, or {@code null} if the proposal does not exist
     * @throws Exception if a database access error occurs
     */
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

    /**
     * Inserts a new sales proposal into the database.
     *
     * <p>This method is typically called from the Sales "New Proposal" flow.
     * The proposal is created with an initial {@code ACTIVE} status as defined
     * in the insert statement.</p>
     *
     * @param customerId the customer associated with the proposal
     * @param vehicleId the vehicle included in the proposal
     * @param sellerUserId the seller user creating the proposal
     * @param dealershipId the dealership where the proposal is created
     * @param price the proposed price for the vehicle
     * @param notes optional notes for the proposal (stored as null when blank)
     * @throws Exception if a database access error occurs
     */
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

    /**
     * Updates price, notes, and status for an existing proposal.
     *
     * <p>This method supports the proposal detail/edit flow in the Sales module,
     * allowing the application to persist user changes back to the database.</p>
     *
     * @param proposalId the proposal identifier to update
     * @param price the new proposal price
     * @param notes the updated notes (stored as null when blank)
     * @param status the new proposal status
     * @throws Exception if a database access error occurs
     */
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

    /**
     * Updates only the status of a proposal.
     *
     * <p>This is used when the Sales module needs to quickly toggle or set
     * the proposal state without modifying price or notes.</p>
     *
     * @param proposalId the proposal identifier to update
     * @param status the status value to store
     * @throws Exception if a database access error occurs
     */
    public void setProposalStatus(int proposalId, String status) throws Exception {
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_SET_STATUS)) {

            ps.setString(1, status);
            ps.setInt(2, proposalId);

            ps.executeUpdate();
        }
    }

    /**
     * Checks whether a proposal has already been converted into a sale.
     *
     * <p>This method is used to enforce deletion rules and prevent removing
     * proposals that are already referenced by a row in the {@code sale} table.</p>
     *
     * @param proposalId the proposal identifier to check
     * @return {@code true} if a sale exists for the given proposal, otherwise {@code false}
     * @throws Exception if a database access error occurs
     */
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
     *
     * @param price price value from database
     * @return formatted price string for tables
     */
    private String formatPrice(BigDecimal price) {
        if (price == null) return "-";
        return price.stripTrailingZeros().toPlainString();
    }
}
