package dealership.model;

import javafx.beans.property.*;

/**
 * Represents a single row displayed in the sales history table
 * within the JavaFX sales module.
 *
 * <p>This class is a UI-focused model that uses JavaFX properties to enable
 * data binding with {@code TableView} columns and other visual components.
 * It contains only presentation-ready values related to completed sales.</p>
 *
 * <p>The class does not implement business logic. Its sole responsibility
 * is to act as a lightweight data holder that adapts sale information
 * retrieved from the backend into a format suitable for JavaFX bindings.</p>
 */
public class SalesSaleRow {

    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty saleCode = new SimpleStringProperty();
    private final StringProperty vehicle = new SimpleStringProperty();
    private final StringProperty customer = new SimpleStringProperty();
    private final StringProperty price = new SimpleStringProperty();

    /**
     * Creates a new {@code SalesSaleRow} instance populated with the values
     * required for display in the sales table.
     *
     * <p>This constructor is typically used when transforming persisted
     * sale data into a UI-friendly representation that can be directly
     * bound to JavaFX table columns.</p>
     *
     * @param id the internal identifier of the sale
     * @param saleCode the visible sale reference or code
     * @param vehicle the human-readable vehicle description
     * @param customer the display name of the customer
     * @param price the formatted price string shown in the UI
     */
    public SalesSaleRow(int id, String saleCode, String vehicle, String customer, String price) {
        this.id.set(id);
        this.saleCode.set(saleCode);
        this.vehicle.set(vehicle);
        this.customer.set(customer);
        this.price.set(price);
    }

    /**
     * Returns the internal identifier of the sale.
     *
     * @return the sale ID
     */
    public int getId() { return id.get(); }

    /**
     * Returns the JavaFX property representing the sale ID.
     *
     * <p>This property is primarily used for binding the identifier
     * to table columns or other UI components.</p>
     *
     * @return the ID property
     */
    public IntegerProperty idProperty() { return id; }

    /**
     * Returns the visible sale reference or code.
     *
     * @return the sale code
     */
    public String getSaleCode() { return saleCode.get(); }

    /**
     * Returns the JavaFX property representing the sale code.
     *
     * @return the sale code property
     */
    public StringProperty saleCodeProperty() { return saleCode; }

    /**
     * Returns the vehicle description displayed in the sales table.
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
     * Returns the customer name associated with the sale.
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
     * Returns the formatted price string displayed for the sale.
     *
     * @return the price text
     */
    public String getPrice() { return price.get(); }

    /**
     * Returns the JavaFX property representing the formatted sale price.
     *
     * <p>The price is stored as a string to match the presentation
     * format used in the sales user interface.</p>
     *
     * @return the price property
     */
    public StringProperty priceProperty() { return price; }
}

