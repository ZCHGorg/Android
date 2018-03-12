package io.charg.chargstation.models.firebase;

/**
 * Created by worker on 01.11.2017.
 */

public class Connection {

    private Integer ID;
    private Integer ConnectionTypeID;
    private Integer StatusTypeID;
    private Integer LevelID;
    private Integer Amps;
    private Integer Voltage;
    private Double PowerKW;
    private Integer CurrentTypeID;
    private Integer Quantity;

    public Connection() {
    }

    public Integer getID() {
        return ID;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

    public Integer getConnectionTypeID() {
        return ConnectionTypeID;
    }

    public void setConnectionTypeID(Integer connectionTypeID) {
        ConnectionTypeID = connectionTypeID;
    }

    public Integer getStatusTypeID() {
        return StatusTypeID;
    }

    public void setStatusTypeID(Integer statusTypeID) {
        StatusTypeID = statusTypeID;
    }

    public Integer getLevelID() {
        return LevelID;
    }

    public void setLevelID(Integer levelID) {
        LevelID = levelID;
    }

    public Integer getAmps() {
        return Amps;
    }

    public void setAmps(Integer amps) {
        Amps = amps;
    }

    public Integer getVoltage() {
        return Voltage;
    }

    public void setVoltage(Integer voltage) {
        Voltage = voltage;
    }

    public Double getPowerKW() {
        return PowerKW;
    }

    public void setPowerKW(Double powerKW) {
        PowerKW = powerKW;
    }

    public Integer getCurrentTypeID() {
        return CurrentTypeID;
    }

    public void setCurrentTypeID(Integer currentTypeID) {
        CurrentTypeID = currentTypeID;
    }

    public Integer getQuantity() {
        return Quantity;
    }

    public void setQuantity(Integer quantity) {
        Quantity = quantity;
    }
}