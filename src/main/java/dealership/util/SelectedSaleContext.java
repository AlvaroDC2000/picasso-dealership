package dealership.util;

/**
 * Stores the selected sale id between screens (list -> detail).
 *
 * <p>This utility class provides a simple static context used to keep
 * track of the currently selected sale identifier across different
 * screens in the Sales module.</p>
 *
 * <p>It is typically used when navigating from a sales list or history
 * view to a sale detail screen, allowing controllers to access the
 * selected sale without directly passing parameters.</p>
 *
 * <p>The stored value is shared application-wide and must be cleared
 * explicitly to prevent unintended reuse.</p>
 */
public class SelectedSaleContext {

    private static Integer saleId;

    /**
     * Private constructor to prevent instantiation.
     *
     * <p>This class is designed as a static context holder and should
     * not be instantiated.</p>
     */
    private SelectedSaleContext() {
    }

    /**
     * Returns the identifier of the currently selected sale.
     *
     * <p>If no sale is currently selected or the context has been
     * cleared, this method returns {@code null}.</p>
     *
     * @return the selected sale ID, or {@code null} if none is set
     */
    public static Integer getSaleId() {
        return saleId;
    }

    /**
     * Stores the identifier of the currently selected sale.
     *
     * <p>This method is typically called when the user selects a sale
     * from a list or history view before navigating to a detail screen.</p>
     *
     * @param id the sale ID to store
     */
    public static void setSaleId(Integer id) {
        saleId = id;
    }

    /**
     * Clears the stored sale identifier.
     *
     * <p>This method should be invoked when leaving the sale detail
     * flow to ensure that no stale selection data is reused.</p>
     */
    public static void clear() {
        saleId = null;
    }
}

