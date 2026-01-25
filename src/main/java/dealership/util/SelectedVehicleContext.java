package dealership.util;

/**
 * Stores the selected vehicle identifier between screens.
 * <p>
 * This context class is used mainly in the Sales module to keep the selected
 * vehicle id when navigating from the vehicles list screen to the vehicle
 * detail screen.
 * </p>
 * <p>
 * The value is stored in a static field so it can be accessed by different
 * controllers without passing parameters explicitly.
 * </p>
 */
public class SelectedVehicleContext {

    private static Integer vehicleId;

    /**
     * Private constructor to prevent instantiation.
     * <p>
     * This class is designed to be used only as a static holder for navigation data.
     * </p>
     */
    private SelectedVehicleContext() {
    }

    /**
     * Returns the currently selected vehicle id.
     *
     * @return the vehicle id, or null if no vehicle is selected
     */
    public static Integer getVehicleId() {
        return vehicleId;
    }

    /**
     * Sets the selected vehicle id.
     *
     * @param id the vehicle id to store
     */
    public static void setVehicleId(Integer id) {
        vehicleId = id;
    }

    /**
     * Clears the stored vehicle id.
     * <p>
     * This should be called when leaving the vehicle detail flow to avoid
     * using outdated navigation data.
     * </p>
     */
    public static void clear() {
        vehicleId = null;
    }
}

