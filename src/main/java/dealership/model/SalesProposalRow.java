package dealership.model;

import javafx.beans.property.*;

/**
 * Represents a single row of data displayed in the sales proposals table
 * within the JavaFX user interface.
 *
 * <p>This class is specifically designed for UI binding purposes. It uses
 * JavaFX properties so that table columns and other UI components can
 * automatically react to data changes.</p>
 *
 * <p>The model contains only presentation-ready values and does not include
 * business logic. It acts as a lightweight adapter between proposal data
 * retrieved from the backend and the JavaFX TableView used in the sales module.</p>
 */
public class SalesProposalRow {

    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty proposalCode = new SimpleStringProperty();
    private final StringProperty vehicle = new SimpleStringProperty();
    private final StringProperty customer = new SimpleStringProperty();
    private final StringProperty price = new SimpleStringProperty();

    /**
     * Creates a new {@code SalesProposalRow} with all values required for display
     * in the sales proposals table.
     *
     * <p>This constructor is typically invoked when transforming proposal
     * domain data into a UI-friendly representation suitable for JavaFX
     * table bindings.</p>
     *
     * @param id the internal identifier of the proposal
     * @param proposalCode the visible proposal reference or code
     * @param vehicle the human-readable vehicle description
     * @param customer the display name of the customer
     * @param price the formatted price string shown in the UI
     */
    public SalesProposalRow(int id, String proposalCode, String vehicle, String customer, String price) {
        this.id.set(id);
        this.proposalCode.set(proposalCode);
        this.vehicle.set(vehicle);
        this.customer.set(customer);
        this.price.set(price);
    }

    /**
     * Returns the proposal identifier value.
     *
     * @return the proposal ID
     */
    public int getId() { return id.get(); }

    /**
     * Returns the JavaFX property representing the proposal ID.
     *
     * <p>This property is mainly used for binding the ID to table columns
     * or other UI components.</p>
     *
     * @return the ID property
     */
    public IntegerProperty idProperty() { return id; }

    /**
     * Returns the proposal reference or code.
     *
     * @return the proposal code
     */
    public String getProposalCode() { return proposalCode.get(); }

    /**
     * Returns the JavaFX property representing the proposal code.
     *
     * @return the proposal code property
     */
    public StringProperty proposalCodeProperty() { return proposalCode; }

    /**
     * Returns the vehicle description displayed in the table.
     *
     * @return the vehicle text
     */
    public String getVehicle() { return vehicle.get(); }

    /**
     * Returns the JavaFX property representing the vehicle description.
     *
     * @return the vehicle property
     */
    public StringProperty vehicleProperty() { return vehicle; }

    /**
     * Returns the customer name displayed in the table.
     *
     * @return the customer name
     */
    public String getCustomer() { return customer.get(); }

    /**
     * Returns the JavaFX property representing the customer name.
     *
     * @return the customer property
     */
    public StringProperty customerProperty() { return customer; }

    /**
     * Returns the formatted price string displayed in the table.
     *
     * @return the price text
     */
    public String getPrice() { return price.get(); }

    /**
     * Returns the JavaFX property representing the formatted price.
     *
     * <p>This value is intentionally stored as a string to match the
     * presentation format used in the sales UI.</p>
     *
     * @return the price property
     */
    public StringProperty priceProperty() { return price; }
}

