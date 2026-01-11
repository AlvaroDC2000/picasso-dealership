package dealership.model;

public class MechanicSkillRow {

    private final int mechanicId;
    private final String mechanicName;
    private final String skills;
    private final String status;

    public MechanicSkillRow(int mechanicId, String mechanicName, String skills, String status) {
        this.mechanicId = mechanicId;
        this.mechanicName = mechanicName;
        this.skills = skills;
        this.status = status;
    }

    public int getMechanicId() {
        return mechanicId;
    }

    public String getMechanicName() {
        return mechanicName;
    }

    public String getSkills() {
        return skills;
    }

    public String getStatus() {
        return status;
    }
}
