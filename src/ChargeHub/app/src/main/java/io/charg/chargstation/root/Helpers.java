package io.charg.chargstation.root;

/**
 * Created by worker on 13.11.2017.
 */

public class Helpers {

    public static double zoomLevelToRadius(double zoomLevel) {
        return 16384000 / Math.pow(2, zoomLevel);
    }
}
