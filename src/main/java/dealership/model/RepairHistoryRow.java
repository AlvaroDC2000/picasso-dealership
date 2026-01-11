package dealership.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class RepairHistoryRow {

    private final IntegerProperty repairId = new SimpleIntegerProperty();
    private final StringProperty vehicle = new SimpleStringProperty();
    private final StringProperty status = new SimpleStringProperty();
    private final StringProperty date = new SimpleStringProperty();

    public RepairHistoryRow(int repairId, String vehicle, String status, String date) {
        this.repairId.set(repairId);
        this.vehicle.set(vehicle);
        this.status.set(status);
        this.date.set(date);
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

    public String getDate() {
        return date.get();
    }

    public StringProperty dateProperty() {
        return date;
    }
}
