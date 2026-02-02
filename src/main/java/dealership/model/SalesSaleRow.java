package dealership.model;

import javafx.beans.property.*;

import java.time.LocalDate;

/**
 * Represents a single row displayed in the sales table
 * within the JavaFX sales module.
 *
 * <p>This class is a UI-focused model designed for JavaFX data binding.
 * It exposes all values as JavaFX properties so table columns and other
 * UI components can automatically reflect data changes.</p>
 *
 * <p>The model contains only presentation-ready data and does not include
 * any business logic. It acts as an adapter between persisted sale data
 * and the JavaFX {@code TableView} used in the Sales module.</p>
 */
public class SalesSaleRow {

    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty saleCode = new SimpleStringProperty();
    private final StringProperty vehicle = new SimpleStringProperty();
    private final StringProperty customer = new SimpleStringProperty();
    private final StringProperty price = new SimpleStringProperty();
    private final ObjectProperty<LocalDate> saleDate = new SimpleObjectProperty<>();

    /**
     * Creates a new {@code SalesSaleRow} with all values required for display
     * in the sales table.
     *
     * <p>This constructor is typically used when mapping sale data retrieved
     * from the database into a UI-friendly structure suitable for JavaFX
     * table bindings.</p>
     *
     * @param id the internal identifier of the sale
     * @param saleCode the visible sale reference or code
     * @param vehicle the human-readable vehicle description
     * @param customer the display name of the customer
     * @param price the formatted price string shown in the UI
     * @param saleDate the date on which the sale was completed
     */
    public SalesSaleRow(int id, String saleCode, String vehicle, String customer, String price, LocalDate saleDate) {
        this.id.set(id);
        this.saleCode.set(saleCode);
        this.vehicle.set(vehicle);
        this.customer.set(customer);
        this.price.set(price);
        this.saleDate.set(saleDate);
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
     * @return the ID property
     */
    public IntegerProperty idProperty() { return id; }

    /**
     * Returns the sale reference or code displayed in the table.
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
     * @return the price property
     */
    public StringProperty priceProperty() { return price; }

    /**
     * Returns the date on which the sale was completed.
     *
     * @return the sale date
     */
    public LocalDate getSaleDate() { return saleDate.get(); }

    /**
     * Returns the JavaFX property representing the sale date.
     *
     * <p>The date is exposed as a {@link LocalDate} to allow correct
     * sorting and formatting in the sales table.</p>
     *
     * @return the sale date property
     */
    public ObjectProperty<LocalDate> saleDateProperty() { return saleDate; }
}

