package dealership.model;

/**
 * Full customer information model used in the Customer Detail screen.
 * <p>
 * This class represents a complete view of a customer, including personal
 * identification data and contact information. It is mainly used by the
 * Sales module when displaying detailed customer information.
 * </p>
 */
public class CustomerDetail {

    private final int id;
    private final String dni;
    private final String firstName;
    private final String lastName;
    private final String phone;
    private final String email;

    /**
     * Creates a new {@code CustomerDetail} instance.
     *
     * @param id the unique identifier of the customer
     * @param dni the customer DNI
     * @param firstName the customer's first name
     * @param lastName the customer's last name
     * @param phone the customer's phone number
     * @param email the customer's email address
     */
    public CustomerDetail(int id, String dni, String firstName, String lastName, String phone, String email) {
        this.id = id;
        this.dni = dni;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.email = email;
    }

    /**
     * Returns the customer identifier.
     *
     * @return the customer id
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the customer DNI.
     *
     * @return the DNI value
     */
    public String getDni() {
        return dni;
    }

    /**
     * Returns the customer's first name.
     *
     * @return the first name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Returns the customer's last name.
     *
     * @return the last name
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Returns the customer's phone number.
     *
     * @return the phone number
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Returns the customer's email address.
     *
     * @return the email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * Returns the customer's full name.
     * <p>
     * This method safely builds the full name by trimming both first and last
     * names and ensuring a readable value is always returned.
     * </p>
     *
     * @return the formatted full name, or "-" if empty
     */
    public String getFullName() {
        String fn = (firstName == null) ? "" : firstName.trim();
        String ln = (lastName == null) ? "" : lastName.trim();
        String full = (fn + " " + ln).trim();
        return full.isBlank() ? "-" : full;
    }
}
