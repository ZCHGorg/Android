package io.charg.chargstation.models.firebase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GeofireDto {

    private String g;

    private List<Double> l;

    public GeofireDto() {
        l = new ArrayList<>(Arrays.asList(0.0, 0.0));
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
