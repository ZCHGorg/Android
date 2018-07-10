package io.charg.chargstation.models.firebase;

/**
 * Created by worker on 01.03.2018.
 */

public class StationDto {

    public static final String STATION_STATE_IDLE = "idle";
    public static final String STATION_STATE_READY = "ready";
    public static final String STATION_STATE_CHARGED = "charged";

    public static final String CLIENT_STATE_REQUEST = "request";
    public static final String CLIENT_STATE_READY = "ready";
    public static final String CLIENT_STATE_CHARGED = "charged";

    private String address;
    private String comments;
    private int connector_type;
    private double cost_charge;
    private double cost_park;
    private String eth_address;
    private String phone;
    private double power;
    private String title;
    private String client_address;
    private String client_state;
    private String station_state;

    public StationDto() {
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public int getConnector_type() {
        return connector_type;
    }

    public void setConnector_type(int connector_type) {
        this.connector_type = connector_type;
    }

    public double getCost_charge() {
        return cost_charge;
    }

    public void setCost_charge(double cost_charge) {
        this.cost_charge = cost_charge;
    }

    public double getCost_park() {
        return cost_park;
    }

    public void setCost_park(double cost_park) {
        this.cost_park = cost_park;
    }

    public String getEth_address() {
        return eth_address;
    }

    public void setEth_address(String eth_address) {
        this.eth_address = eth_address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public double getPower() {
        return power;
    }

    public void setPower(double power) {
        this.power = power;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getClient_address() {
        return client_address;
    }

    public void setClient_address(String client_address) {
        this.client_address = client_address;
    }

    public String getClient_state() {
        return client_state;
    }

    public void setClient_state(String client_state) {
        this.client_state = client_state;
    }

    public String getStation_state() {
        return station_state;
    }

    public void setStation_state(String station_state) {
        this.station_state = station_state;
    }
}
