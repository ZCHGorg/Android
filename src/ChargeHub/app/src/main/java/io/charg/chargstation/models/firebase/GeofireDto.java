package io.charg.chargstation.models.firebase;

import java.util.List;

public class GeofireDto {

    private String g;

    private List<Double> l;

    public GeofireDto() {
    }

    public String getG() {
        return g;
    }

    public void setG(String g) {
        this.g = g;
    }

    public List<Double> getL() {
        return l;
    }

    public void setL(List<Double> l) {
        this.l = l;
    }

    public double getLat() {
        return l.get(0);
    }

    public double getLng() {
        return l.get(1);
    }
}
