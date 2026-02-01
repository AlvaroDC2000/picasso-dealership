package dealership.model;

import java.math.BigDecimal;

/**
 * Represents a read-only detailed view of a sales proposal within the dealership application.
 *
 * <p>This model aggregates proposal data together with human-readable customer and vehicle
 * information, making it suitable for use in the sales UI, listings, and detail views where
 * a complete proposal context is required.</p>
 *
 * <p>The class is immutable by design: all fields are final and values are provided
 * at construction time. It is typically populated from database queries that join
 * proposal, customer, and vehicle data.</p>
 */
public class ProposalDetail {

    private final int id;
    private final int customerId;
    private final int vehicleId;

    private final String customerName;
    private final String vehicleText;

    private final BigDecimal price;
    private final String notes;
    private final String status;

    /**
     * Creates a new {@code ProposalDetail} instance with all proposal-related information.
     *
     * <p>This constructor is used to fully initialize the proposal detail object, usually
     * when mapping the result of a database query to a domain model that will be consumed
     * by the sales layer of the application.</p>
     *
     * @param id the unique identifier of the proposal
     * @param customerId the identifier of the customer associated with the proposal
     * @param vehicleId the identifier of the vehicle included in the proposal
     * @param customerName the display name of the customer
     * @param vehicleText a human-readable description of the vehicle
     * @param price the proposed sale price for the vehicle
     * @param notes optional notes or comments associated with the proposal
     * @param status the current status of the proposal (e.g. pending, accepted, rejected)
     */
    public ProposalDetail(int id,
                          int customerId,
                          int vehicleId,
                          String customerName,
                          String vehicleText,
                          BigDecimal price,
                          String notes,
                          String status) {
        this.id = id;
        this.customerId = customerId;
        this.vehicleId = vehicleId;
        this.customerName = customerName;
        this.vehicleText = vehicleText;
        this.price = price;
        this.notes = notes;
        this.status = status;
    }

    /**
     * Returns the unique identifier of the proposal.
     *
     * @return the proposal ID
     */
    public int getId() { return id; }

    /**
     * Returns the identifier of the customer linked to the proposal.
     *
     * <p>This value is typically used internally to relate the proposal
     * to the corresponding customer entity.</p>
     *
     * @return the customer ID
     */
    public int getCustomerId() { return customerId; }

    /**
     * Returns the identifier of the vehicle associated with the proposal.
     *
     * <p>This value allows the application to reference the underlying vehicle
     * entity when navigating or processing proposal data.</p>
     *
     * @return the vehicle ID
     */
    public int getVehicleId() { return vehicleId; }

    /**
     * Returns the display name of the customer.
     *
     * <p>This value is intended for presentation purposes in the sales UI,
     * avoiding the need to resolve customer data separately.</p>
     *
     * @return the customer name
     */
    public String getCustomerName() { return customerName; }

    /**
     * Returns a human-readable description of the vehicle.
     *
     * <p>This text is typically composed of relevant vehicle attributes
     * (such as brand, model, or plate) and is meant for direct display
     * in proposal listings and detail views.</p>
     *
     * @return the vehicle description text
     */
    public String getVehicleText() { return vehicleText; }

    /**
     * Returns the proposed sale price of the vehicle.
     *
     * <p>The price is represented using {@link BigDecimal} to ensure
     * precision in monetary calculations.</p>
     *
     * @return the proposal price
     */
    public BigDecimal getPrice() { return price; }

    /**
     * Returns any notes associated with the proposal.
     *
     * <p>Notes may include additional information, comments, or clarifications
     * provided during the proposal creation process.</p>
     *
     * @return the proposal notes, or {@code null} if none were provided
     */
    public String getNotes() { return notes; }

    /**
     * Returns the current status of the proposal.
     *
     * <p>The status reflects the lifecycle state of the proposal within
     * the sales process.</p>
     *
     * @return the proposal status
     */
    public String getStatus() { return status; }
}

