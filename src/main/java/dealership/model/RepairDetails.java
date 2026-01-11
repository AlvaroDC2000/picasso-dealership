package dealership.model;

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

    public int getRepairId() {
        return repairId;
    }

    public String getStatus() {
        return status;
    }

    public String getNotes() {
        return notes;
    }

    public int getCustomerId() {
        return customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getCustomerDni() {
        return customerDni;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public String getVehicleText() {
        return vehicleText;
    }
}
