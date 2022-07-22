package model;

import com.google.gson.Gson;
import java.time.LocalDateTime;

public class TripDuration {
    private final Long sumDurations;
    private final Long numberOfTrips;
    private final double km_passed;
    private final LocalDateTime date;

    private static final Gson gson = new Gson();

    public TripDuration(Long sumDurations, Long numberOfTrips, double km_passed, LocalDateTime date) {
        this.sumDurations = sumDurations;
        this.numberOfTrips = numberOfTrips;
        this.km_passed = km_passed;
        this.date = date;
    }

    @Override
    public String toString() {
        return gson.toJson(this);
    }
}
