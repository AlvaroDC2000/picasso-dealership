package dealership.model;

/**
 * Model representing repair details used in the boss repair edit screen.
 * <p>
 * This class aggregates all the information the boss needs to view and edit
 * a repair: basic repair data, vehicle description, current status and notes,
 * plus optional information about the assigned mechanic.
 * </p>
 */
public class BossRepairEditDetails {

    private final int repairId;
    private final String vehicleText;
    private final String status;
    private final String notes;

    private final Integer assignedMechanicId;
    private final String assignedMechanicName;

    /**
     * Creates a new instance containing all editable repair details for the boss.
     *
     * @param repairId the repair identifier
     * @param vehicleText formatted vehicle description
     * @param status current repair status
     * @param notes repair notes
     * @param assignedMechanicId the assigned mechanic ID, or null if none
     * @param assignedMechanicName the assigned mechanic name, or empty if none
     */
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

    /**
     * Returns the repair identifier.
     *
     * @return the repair ID
     */
    public int getRepairId() {
        return repairId;
    }

    /**
     * Returns the formatted vehicle description.
     *
     * @return vehicle text
     */
    public String getVehicleText() {
        return vehicleText;
    }

    /**
     * Returns the current repair status.
     *
     * @return repair status
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
     * Returns the assigned mechanic ID.
     *
     * @return mechanic ID, or null if not assigned
     */
    public Integer getAssignedMechanicId() {
        return assignedMechanicId;
    }

    /**
     * Returns the assigned mechanic name.
     *
     * @return mechanic name, or empty if not assigned
     */
    public String getAssignedMechanicName() {
        return assignedMechanicName;
    }
}

