package dealership.model;

public class BossRepairEditDetails {

    private final int repairId;
    private final String vehicleText;
    private final String status;
    private final String notes;

    private final Integer assignedMechanicId;
    private final String assignedMechanicName;

    public BossRepairEditDetails(int repairId,
                                String vehicleText,
                                String status,
                                String notes,
                                Integer assignedMechanicId,
                                String assignedMechanicName) {
        this.repairId = repairId;
        this.vehicleText = vehicleText;
        this.status = status;
        this.notes = notes;
        this.assignedMechanicId = assignedMechanicId;
        this.assignedMechanicName = assignedMechanicName;
    }

    public int getRepairId() {
        return repairId;
    }

    public String getVehicleText() {
        return vehicleText;
    }

    public String getStatus() {
        return status;
    }

    public String getNotes() {
        return notes;
    }

    public Integer getAssignedMechanicId() {
        return assignedMechanicId;
    }

    public String getAssignedMechanicName() {
        return assignedMechanicName;
    }
}
