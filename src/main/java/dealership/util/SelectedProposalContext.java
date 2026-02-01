package dealership.util;

/**
 * Stores the selected proposal id between screens (list -> detail).
 *
 * <p>This utility class acts as a simple in-memory context to share
 * the currently selected proposal identifier across different
 * screens of the Sales module.</p>
 *
 * <p>It is commonly used when navigating from a proposals list view
 * to a proposal detail view, avoiding the need to pass parameters
 * directly between controllers.</p>
 *
 * <p>The stored value is static and must be cleared explicitly
 * once it is no longer needed.</p>
 */
public class SelectedProposalContext {

    private static Integer proposalId;

    /**
     * Private constructor to prevent instantiation.
     *
     * <p>This class is intended to be used as a static context holder
     * and should never be instantiated.</p>
     */
    private SelectedProposalContext() {
    }

    /**
     * Returns the currently selected proposal identifier.
     *
     * <p>If no proposal has been selected or the context has been
     * cleared, this method returns {@code null}.</p>
     *
     * @return the selected proposal ID, or {@code null} if none is set
     */
    public static Integer getProposalId() {
        return proposalId;
    }

    /**
     * Stores the identifier of the currently selected proposal.
     *
     * <p>This method is typically called when the user selects
     * a proposal from a list and navigates to a detail screen.</p>
     *
     * @param id the proposal ID to store
     */
    public static void setProposalId(Integer id) {
        proposalId = id;
    }

    /**
     * Clears the stored proposal identifier.
     *
     * <p>This method should be called when leaving the proposal
     * detail flow to avoid reusing stale selection data.</p>
     */
    public static void clear() {
        proposalId = null;
    }
}
