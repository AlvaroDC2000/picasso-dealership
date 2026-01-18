package dealership.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Model representing a single row in the mechanic repair history table.
 * <p>
 * This class uses JavaFX properties so it can be directly bound to
 * {@link javafx.scene.control.TableView} columns. Each instance represents
 * one completed repair with basic display information.
 * </p>
 */
public class RepairHistoryRow {

    private final IntegerProperty repairId = new SimpleIntegerProperty();
    private final StringProperty vehicle = new SimpleStringProperty();
    private final StringProperty status = new SimpleStringProperty();
    private final StringProperty date = new SimpleStringProperty();

    /**
     * Creates a new repair history row.
     *
     * @param repairId the repair identifier
     * @param vehicle the formatted vehicle description
     * @param status the repair status text (e.g. Completed)
     * @param date the formatted completion date
     */
    public RepairHistoryRow(int repairId, String vehicle, String status, String date) {
        this.repairId.set(repairId);
        this.vehicle.set(vehicle);
        this.status.set(status);
        this.date.set(date);
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

    /**
     * Returns the repair completion date.
     *
     * @return date text
     */
    public String getDate() {
        return date.get();
    }

    /**
     * Returns the JavaFX property for the repair date.
     *
     * @return date property
     */
    public StringProperty dateProperty() {
        return date;
    }
}
