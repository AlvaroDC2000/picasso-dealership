package dealership.util;

/**
 * Utility class used to keep the selected repair ID between screens.
 * <p>
 * This context works as a simple shared holder to store the current repair
 * identifier while navigating between different views. It avoids having
 * to pass the repair ID explicitly through every controller.
 * </p>
 */
public class RepairSelectionContext {

    private static Integer selectedRepairId;

    /**
     * Private constructor to prevent instantiation.
     * <p>
     * This class is intended to be used only through its static methods.
     * </p>
     */
    private RepairSelectionContext() {
    }

    /**
     * Returns the currently selected repair ID.
     *
     * @return the selected repair ID, or null if none is set
     */
    public static Integer getSelectedRepairId() {
        return selectedRepairId;
    }

    /**
     * Sets the selected repair ID.
     *
     * @param selectedRepairId the repair ID to store
     */
    public static void setSelectedRepairId(Integer selectedRepairId) {
        RepairSelectionContext.selectedRepairId = selectedRepairId;
    }

    /**
     * Clears the stored repair selection.
     * <p>
     * After calling this method, no repair will be considered selected.
     * </p>
     */
    public static void clear() {
        selectedRepairId = null;
    }
}
