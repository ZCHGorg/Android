package io.charg.chargstation.models.firebase;

public class GeofireDto {

    private String g;

    private Double[] l;

    public GeofireDto() {
    }

    public double getLat() {
        return l[0];
    }

    public double getLng() {
        return l[1];
    }
}
