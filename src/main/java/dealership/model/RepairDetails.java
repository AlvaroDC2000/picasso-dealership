package dealership.model;

/**
 * Model representing full repair details.
 * <p>
 * This class aggregates all information related to a repair that is needed
 * in the mechanic workflow and customer details screens. It includes repair
 * status and notes, customer contact information, and the related vehicle text.
 * </p>
 */
public class RepairDetails {

    private final int repairId;
    private final String status;
    private final String notes;

    private final int customerId;
    private final String customerName;
    private final String customerDni;
    private final String customerPhone;
    private final String customerEmail;

    private final String vehicleText;

    /**
     * Creates a new repair details instance.
     *
     * @param repairId the repair identifier
     * @param status the current repair status
     * @param notes the repair notes
     * @param customerId the customer identifier
     * @param customerName the customer full name
     * @param customerDni the customer DNI
     * @param customerPhone the customer phone number
     * @param customerEmail the customer email address
     * @param vehicleText the formatted vehicle description
     */
    public RepairDetails(int repairId,
                         String status,
                         String notes,
                         int customerId,
                         String customerName,
                         String customerDni,
                         String customerPhone,
                         String customerEmail,
                         String vehicleText) {
        this.repairId = repairId;
        this.status = status;
        this.notes = notes;
        this.customerId = customerId;
        this.customerName = customerName;
        this.customerDni = customerDni;
        this.customerPhone = customerPhone;
        this.customerEmail = customerEmail;
        this.vehicleText = vehicleText;
    }

    /**
     * Returns the repair identifier.
     *
     * @return repair ID
     */
    public int getRepairId() {
        return repairId;
    }

    /**
     * Returns the current repair status.
     *
     * @return status text
     */
    public String getStatus() {
        return status;
    }

    /**
     * Returns the repair notes.
     *
     * @return notes text
     */
    public String getNotes() {
        return notes;
    }

    /**
     * Returns the customer identifier.
     *
     * @return customer ID
     */
    public int getCustomerId() {
        return customerId;
    }

    /**
     * Returns the customer full name.
     *
     * @return customer name
     */
    public String getCustomerName() {
        return customerName;
    }

    /**
     * Returns the customer DNI.
     *
     * @return customer DNI
     */
    public String getCustomerDni() {
        return customerDni;
    }

    /**
     * Returns the customer phone number.
     *
     * @return customer phone
     */
    public String getCustomerPhone() {
        return customerPhone;
    }

    /**
     * Returns the customer email address.
     *
     * @return customer email
     */
    public String getCustomerEmail() {
        return customerEmail;
    }

    /**
     * Returns the formatted vehicle description.
     *
     * @return vehicle text
     */
    public String getVehicleText() {
        return vehicleText;
    }
}

