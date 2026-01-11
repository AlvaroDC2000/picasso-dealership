package dealership.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class RepairTaskRow {

    private final IntegerProperty repairId = new SimpleIntegerProperty();
    private final StringProperty vehicle = new SimpleStringProperty();
    private final StringProperty status = new SimpleStringProperty();

    public RepairTaskRow(int repairId, String vehicle, String status) {
        this.repairId.set(repairId);
        this.vehicle.set(vehicle);
        this.status.set(status);
    }

    public int getRepairId() {
        return repairId.get();
    }

    public IntegerProperty repairIdProperty() {
        return repairId;
    }

    public String getVehicle() {
        return vehicle.get();
    }

    public StringProperty vehicleProperty() {
        return vehicle;
    }

    public String getStatus() {
        return status.get();
    }

    public StringProperty statusProperty() {
        return status;
    }
}
