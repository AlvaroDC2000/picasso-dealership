package dealership.model;

/**
 * Model representing a mechanic row in the boss skills table.
 * <p>
 * This class is used to display mechanic information in table views,
 * including the mechanic identity, skill description and current
 * active/inactive status.
 * </p>
 */
public class MechanicSkillRow {

    private final int mechanicId;
    private final String mechanicName;
    private final String skills;
    private final String status;

    /**
     * Creates a new mechanic skills row.
     *
     * @param mechanicId the mechanic user ID
     * @param mechanicName the full name of the mechanic
     * @param skills the skills description text
     * @param status the mechanic status (e.g. Active / Inactive)
     */
    public MechanicSkillRow(int mechanicId, String mechanicName, String skills, String status) {
        this.mechanicId = mechanicId;
        this.mechanicName = mechanicName;
        this.skills = skills;
        this.status = status;
    }

    /**
     * Returns the mechanic identifier.
     *
     * @return mechanic ID
     */
    public int getMechanicId() {
        return mechanicId;
    }

    /**
     * Returns the mechanic full name.
     *
     * @return mechanic name
     */
    public String getMechanicName() {
        return mechanicName;
    }

    /**
     * Returns the mechanic skills description.
     *
     * @return skills text
     */
    public String getSkills() {
        return skills;
    }

    /**
     * Returns the mechanic status text.
     *
     * @return status text
     */
    public String getStatus() {
        return status;
    }
}

