package dealership.model;

import javafx.beans.property.*;
import java.time.LocalDate;

/**
 * Simple row model used in the Sales -> Vehicles table.
 * <p>
 * This class represents a lightweight view of a vehicle, optimized for
 * JavaFX TableView usage. It exposes its fields as JavaFX properties so
 * the UI can bind, observe, and sort values correctly.
 * </p>
 */
public class SalesVehicleRow {

    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty plate = new SimpleStringProperty();
    private final StringProperty vehicle = new SimpleStringProperty();
    private final ObjectProperty<LocalDate> dateAdded = new SimpleObjectProperty<>();

    /**
     * Creates a new row instance for the Sales vehicles table.
     *
     * @param id the vehicle identifier
     * @param plate the vehicle plate number
     * @param vehicle the vehicle display text (brand + model)
     * @param dateAdded the date when the vehicle was added to the system
     */
    public SalesVehicleRow(int id, String plate, String vehicle, LocalDate dateAdded) {
        this.id.set(id);
        this.plate.set(plate);
        this.vehicle.set(vehicle);
        this.dateAdded.set(dateAdded);
    }

    /**
     * Returns the vehicle id.
     *
     * @return the vehicle id
     */
    public int getId() {
        return id.get();
    }

    /**
     * Property accessor for the vehicle id.
     *
     * @return the id property
     */
    public IntegerProperty idProperty() {
        return id;
    }

    /**
     * Returns the vehicle plate.
     *
     * @return the plate text
     */
    public String getPlate() {
        return plate.get();
    }

    /**
     * Property accessor for the vehicle plate.
     *
     * @return the plate property
     */
    public StringProperty plateProperty() {
        return plate;
    }

    /**
     * Returns the vehicle display text.
     *
     * @return the vehicle text
     */
    public String getVehicle() {
        return vehicle.get();
    }

    /**
     * Property accessor for the vehicle text.
     *
     * @return the vehicle property
     */
    public StringProperty vehicleProperty() {
        return vehicle;
    }

    /**
     * Returns the date when the vehicle was added.
     *
     * @return the added date
     */
    public LocalDate getDateAdded() {
        return dateAdded.get();
    }

    /**
     * Property accessor for the vehicle added date.
     *
     * @return the dateAdded property
     */
    public ObjectProperty<LocalDate> dateAddedProperty() {
        return dateAdded;
    }
}
