package dealership.util;

/**
 * Utility class used to keep the selected mechanic ID between screens.
 * <p>
 * This context acts as a simple in-memory holder to pass the mechanic
 * identifier when navigating between different views, avoiding the need
 * to reload or reselect the mechanic each time.
 * </p>
 */
public class MechanicSelectionContext {

    private static Integer selectedMechanicId;

    /**
     * Private constructor to prevent instantiation.
     * <p>
     * This class is intended to be used only through its static methods.
     * </p>
     */
    private MechanicSelectionContext() {
    }

    /**
     * Returns the currently selected mechanic ID.
     *
     * @return the selected mechanic ID, or null if none is set
     */
    public static Integer getSelectedMechanicId() {
        return selectedMechanicId;
    }

    /**
     * Sets the selected mechanic ID.
     *
     * @param selectedMechanicId the mechanic ID to store
     */
    public static void setSelectedMechanicId(Integer selectedMechanicId) {
        MechanicSelectionContext.selectedMechanicId = selectedMechanicId;
    }

    /**
     * Clears the stored mechanic selection.
     * <p>
     * After calling this method, no mechanic will be considered selected.
     * </p>
     */
    public static void clear() {
        selectedMechanicId = null;
    }
}
