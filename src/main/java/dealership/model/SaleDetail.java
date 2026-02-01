package dealership.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class SaleDetail {

    private final int id;
    private final String customerName;
    private final String vehicleText;
    private final BigDecimal price;
    private final LocalDate saleDate;
    private final String notes;

    public SaleDetail(int id, String customerName, String vehicleText,
                      BigDecimal price, LocalDate saleDate, String notes) {
        this.id = id;
        this.customerName = customerName;
        this.vehicleText = vehicleText;
        this.price = price;
        this.saleDate = saleDate;
        this.notes = notes;
    }

    public int getId() { return id; }
    public String getCustomerName() { return customerName; }
    public String getVehicleText() { return vehicleText; }
    public BigDecimal getPrice() { return price; }
    public LocalDate getSaleDate() { return saleDate; }
    public String getNotes() { return notes; }
}
