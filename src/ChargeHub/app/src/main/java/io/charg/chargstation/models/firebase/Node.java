package io.charg.chargstation.models.firebase;

/**
 * Created by worker on 01.03.2018.
 */

public class Node {

    public static final String STATION_STATE_IDLE = "idle";
    public static final String STATION_STATE_READY = "ready";
    public static final String STATION_STATE_CHARGED = "charged";

    public static final String CLIENT_STATE_REQUEST = "request";
    public static final String CLIENT_STATE_READY = "ready";
    public static final String CLIENT_STATE_CHARGED = "charged";

    private String client_address;

    private String client_state;

    private String station_state;

    public Node() {
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
