package dealership.model;

import java.time.LocalDate;

/**
 * Full vehicle information model used in the Vehicle Detail screen.
 * <p>
 * This class represents a complete view of a vehicle, combining both
 * basic identification data and detailed technical specifications.
 * It is mainly used by the Sales module when displaying full vehicle
 * details to the user.
 * </p>
 */
public class VehicleDetail {

    private final int id;
    private final String plate;
    private final String brand;
    private final String model;
    private final Integer year;
    private final String color;
    private final Integer mileage;
    private final String notes;

    private final String type;
    private final String fuel;
    private final String transmission;
    private final Integer doors;
    private final LocalDate entryDate;

    /**
     * Creates a new {@code VehicleDetail} instance.
     *
     * @param id the unique identifier of the vehicle
     * @param plate the vehicle plate number
     * @param brand the vehicle brand
     * @param model the vehicle model
     * @param year the manufacturing year
     * @param color the vehicle color
     * @param mileage the vehicle mileage in kilometers
     * @param notes additional notes related to the vehicle
     * @param type the vehicle type (e.g. car, van, SUV)
     * @param fuel the fuel type (e.g. petrol, diesel, electric)
     * @param transmission the transmission type (e.g. manual, automatic)
     * @param doors the number of doors
     * @param entryDate the date when the vehicle was added to the system
     */
    public VehicleDetail(int id,
                         String plate,
                         String brand,
                         String model,
                         Integer year,
                         String color,
                         Integer mileage,
                         String notes,
                         String type,
                         String fuel,
                         String transmission,
                         Integer doors,
                         LocalDate entryDate) {

        this.id = id;
        this.plate = plate;
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.color = color;
        this.mileage = mileage;
        this.notes = notes;
        this.type = type;
        this.fuel = fuel;
        this.transmission = transmission;
        this.doors = doors;
        this.entryDate = entryDate;
    }

    /**
     * Returns the vehicle identifier.
     *
     * @return the vehicle id
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the vehicle plate number.
     *
     * @return the plate number
     */
    public String getPlate() {
        return plate;
    }

    /**
     * Returns the vehicle brand.
     *
     * @return the brand name
     */
    public String getBrand() {
        return brand;
    }

    /**
     * Returns the vehicle model.
     *
     * @return the model name
     */
    public String getModel() {
        return model;
    }

    /**
     * Returns the manufacturing year of the vehicle.
     *
     * @return the year, or null if not defined
     */
    public Integer getYear() {
        return year;
    }

    /**
     * Returns the vehicle color.
     *
     * @return the color
     */
    public String getColor() {
        return color;
    }

    /**
     * Returns the vehicle mileage.
     *
     * @return the mileage in kilometers
     */
    public Integer getMileage() {
        return mileage;
    }

    /**
     * Returns additional notes about the vehicle.
     *
     * @return the notes text
     */
    public String getNotes() {
        return notes;
    }

    /**
     * Returns the vehicle type.
     *
     * @return the vehicle type
     */
    public String getType() {
        return type;
    }

    /**
     * Returns the fuel type of the vehicle.
     *
     * @return the fuel type
     */
    public String getFuel() {
        return fuel;
    }

    /**
     * Returns the transmission type.
     *
     * @return the transmission type
     */
    public String getTransmission() {
        return transmission;
    }

    /**
     * Returns the number of doors.
     *
     * @return the number of doors
     */
    public Integer getDoors() {
        return doors;
    }

    /**
     * Returns the entry date of the vehicle.
     *
     * @return the entry date
     */
    public LocalDate getEntryDate() {
        return entryDate;
    }
}


