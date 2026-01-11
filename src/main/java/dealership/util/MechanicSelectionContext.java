package dealership.util;

/**
 *  Sirve para conservar el id del mecanico entre pantallas
 */
public class MechanicSelectionContext {

    private static Integer selectedMechanicId;

    private MechanicSelectionContext() {
    }

    public static Integer getSelectedMechanicId() {
        return selectedMechanicId;
    }

    public static void setSelectedMechanicId(Integer selectedMechanicId) {
        MechanicSelectionContext.selectedMechanicId = selectedMechanicId;
    }

    public static void clear() {
        selectedMechanicId = null;
    }
}
