package dealership.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Model representing a repair task row.
 * <p>
 * This class is used in table views to display basic information about
 * a repair task, such as its identifier, the related vehicle and the
 * current repair status. JavaFX properties are used so the data can
 * be easily bound to UI components.
 * </p>
 */
public class RepairTaskRow {

    private final IntegerProperty repairId = new SimpleIntegerProperty();
    private final StringProperty vehicle = new SimpleStringProperty();
    private final StringProperty status = new SimpleStringProperty();

    /**
     * Creates a new repair task row.
     *
     * @param repairId the repair identifier
     * @param vehicle the formatted vehicle description
     * @param status the current repair status
     */
    public RepairTaskRow(int repairId, String vehicle, String status) {
        this.repairId.set(repairId);
        this.vehicle.set(vehicle);
        this.status.set(status);
    }

    /**
     * Returns the repair identifier.
     *
     * @return repair ID
     */
    public int getRepairId() {
        return repairId.get();
    }

    /**
     * Returns the JavaFX property for the repair ID.
     *
     * @return repairId property
     */
    public IntegerProperty repairIdProperty() {
        return repairId;
    }

    /**
     * Returns the vehicle description.
     *
     * @return vehicle text
     */
    public String getVehicle() {
        return vehicle.get();
    }

    /**
     * Returns the JavaFX property for the vehicle description.
     *
     * @return vehicle property
     */
    public StringProperty vehicleProperty() {
        return vehicle;
    }

    /**
     * Returns the repair status text.
     *
     * @return status text
     */
    public String getStatus() {
        return status.get();
    }

    /**
     * Returns the JavaFX property for the repair status.
     *
     * @return status property
     */
    public StringProperty statusProperty() {
        return status;
    }
}
