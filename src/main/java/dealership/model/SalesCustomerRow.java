package dealership.model;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * Lightweight row model used in the Sales -> Customers table.
 * <p>
 * This class is designed specifically for table views in JavaFX.
 * It wraps customer data using JavaFX properties so the UI can
 * observe and react to changes automatically.
 * </p>
 */
public class SalesCustomerRow {

    private final SimpleIntegerProperty id;
    private final SimpleStringProperty fullName;
    private final SimpleStringProperty email;
    private final SimpleStringProperty phone;

    /**
     * Creates a new row instance for the Sales customers table.
     *
     * @param id the customer identifier
     * @param fullName the full name of the customer
     * @param email the customer's email address
     * @param phone the customer's phone number
     */
    public SalesCustomerRow(int id, String fullName, String email, String phone) {
        this.id = new SimpleIntegerProperty(id);
        this.fullName = new SimpleStringProperty(fullName);
        this.email = new SimpleStringProperty(email);
        this.phone = new SimpleStringProperty(phone);
    }

    /**
     * Returns the customer id.
     *
     * @return the customer id
     */
    public int getId() {
        return id.get();
    }

    /**
     * Property accessor for the customer id.
     *
     * @return the id property
     */
    public SimpleIntegerProperty idProperty() {
        return id;
    }

    /**
     * Returns the customer's full name.
     *
     * @return the full name
     */
    public String getFullName() {
        return fullName.get();
    }

    /**
     * Property accessor for the customer's full name.
     *
     * @return the full name property
     */
    public SimpleStringProperty fullNameProperty() {
        return fullName;
    }

    /**
     * Returns the customer's email address.
     *
     * @return the email address
     */
    public String getEmail() {
        return email.get();
    }

    /**
     * Property accessor for the customer's email.
     *
     * @return the email property
     */
    public SimpleStringProperty emailProperty() {
        return email;
    }

    /**
     * Returns the customer's phone number.
     *
     * @return the phone number
     */
    public String getPhone() {
        return phone.get();
    }

    /**
     * Property accessor for the customer's phone number.
     *
     * @return the phone property
     */
    public SimpleStringProperty phoneProperty() {
        return phone;
    }
}
