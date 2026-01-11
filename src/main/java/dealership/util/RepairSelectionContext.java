package dealership.util;

/**
 * Sirve para conservar el id de la reparación entre pantallas
 */
public class RepairSelectionContext {

    private static Integer selectedRepairId;

    private RepairSelectionContext() {
    }

    public static Integer getSelectedRepairId() {
        return selectedRepairId;
    }

    public static void setSelectedRepairId(Integer selectedRepairId) {
        RepairSelectionContext.selectedRepairId = selectedRepairId;
    }

    public static void clear() {
        selectedRepairId = null;
    }
}
