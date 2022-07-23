package model;

import com.google.gson.Gson;

public class TripCount {
    private final double startLatitude;
    private final double startLongitude;
    private final double endLatitude;
    private final double endLongitude;
    private final double numberOfTrips;

    private static final Gson gson = new Gson();

    public TripCount(double startLatitude, double startLongitude, double endLatitude, double endLongitude, double numberOfTrips) {
        this.startLatitude = startLatitude;
        this.startLongitude = startLongitude;
        this.endLatitude = endLatitude;
        this.endLongitude = endLongitude;
        this.numberOfTrips = numberOfTrips;
    }

    @Override
    public String toString() {
        return gson.toJson(this);
    }
}
