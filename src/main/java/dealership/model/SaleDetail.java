package dealership.model;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Represents a detailed, read-only view of a completed sale within the dealership application.
 *
 * <p>This class is designed to provide all relevant information about a sale in a single
 * structure, combining customer, vehicle, and transactional data for use in the sales
 * history screens, reports, and detail views.</p>
 *
 * <p>The model is immutable and is typically populated from database queries that join
 * sales, customers, and vehicles in order to present a user-friendly representation
 * of completed transactions.</p>
 */
public class SaleDetail {

    private final int id;
    private final String customerName;
    private final String vehicleText;
    private final BigDecimal price;
    private final LocalDate saleDate;
    private final String notes;

    /**
     * Creates a new {@code SaleDetail} instance with all relevant sale information.
     *
     * <p>This constructor is generally used when mapping persisted sale data into a
     * presentation-friendly model that can be consumed directly by the sales
     * user interface.</p>
     *
     * @param id the unique identifier of the sale
     * @param customerName the display name of the customer involved in the sale
     * @param vehicleText a human-readable description of the sold vehicle
     * @param price the final sale price of the vehicle
     * @param saleDate the date on which the sale was completed
     * @param notes optional notes or comments associated with the sale
     */
    public SaleDetail(int id, String customerName, String vehicleText,
                      BigDecimal price, LocalDate saleDate, String notes) {
        this.id = id;
        this.customerName = customerName;
        this.vehicleText = vehicleText;
        this.price = price;
        this.saleDate = saleDate;
        this.notes = notes;
    }

    /**
     * Returns the unique identifier of the sale.
     *
     * @return the sale ID
     */
    public int getId() { return id; }

    /**
     * Returns the display name of the customer who completed the purchase.
     *
     * <p>This value is intended for direct use in the sales UI and reports,
     * avoiding additional lookups for customer information.</p>
     *
     * @return the customer name
     */
    public String getCustomerName() { return customerName; }

    /**
     * Returns a human-readable description of the sold vehicle.
     *
     * <p>The returned text is formatted for presentation purposes and
     * typically includes key vehicle identifiers.</p>
     *
     * @return the vehicle description text
     */
    public String getVehicleText() { return vehicleText; }

    /**
     * Returns the final price at which the vehicle was sold.
     *
     * <p>The price is represented using {@link BigDecimal} to preserve
     * accuracy in monetary values.</p>
     *
     * @return the sale price
     */
    public BigDecimal getPrice() { return price; }

    /**
     * Returns the date on which the sale was completed.
     *
     * <p>This date represents the official closing date of the transaction
     * within the dealership.</p>
     *
     * @return the sale date
     */
    public LocalDate getSaleDate() { return saleDate; }

    /**
     * Returns any notes associated with the sale.
     *
     * <p>Notes may include additional remarks, internal comments, or
     * contextual information relevant to the completed transaction.</p>
     *
     * @return the sale notes, or {@code null} if none were provided
     */
    public String getNotes() { return notes; }
}

