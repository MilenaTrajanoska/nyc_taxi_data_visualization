package model;

import com.google.gson.Gson;
import java.time.LocalDateTime;

public class TripDuration {
    private final Long averageDuration;
    private final LocalDateTime date;
    private static final Gson gson = new Gson();

    public TripDuration(Long averageDuration, LocalDateTime date) {
        this.averageDuration = averageDuration;
        this.date = date;
    }

    @Override
    public String toString() {
        return gson.toJson(this);
    }
}
