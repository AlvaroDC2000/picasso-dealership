package dealership.util;

/**
 * Stores the selected customer identifier between screens.
 * <p>
 * This context class is mainly used in the Sales module to keep track of
 * the currently selected customer when navigating from the customers list
 * screen to the customer detail screen.
 * </p>
 * <p>
 * It uses static fields so the value can be accessed from different
 * controllers without passing parameters directly.
 * </p>
 */
public class SelectedCustomerContext {

    private static Integer customerId;

    /**
     * Private constructor to prevent instantiation.
     * <p>
     * This class is intended to be used only as a static context holder.
     * </p>
     */
    private SelectedCustomerContext() {
    }

    /**
     * Returns the currently selected customer id.
     *
     * @return the customer id, or null if none is selected
     */
    public static Integer getCustomerId() {
        return customerId;
    }

    /**
     * Sets the selected customer id.
     *
     * @param id the customer id to store
     */
    public static void setCustomerId(Integer id) {
        customerId = id;
    }

    /**
     * Clears the stored customer id.
     * <p>
     * This should be called when leaving the customer detail flow
     * to avoid using stale navigation data.
     * </p>
     */
    public static void clear() {
        customerId = null;
    }
}

