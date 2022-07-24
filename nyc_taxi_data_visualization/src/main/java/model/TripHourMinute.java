package model;

import com.google.gson.Gson;

public class TripHourMinute {
    private final int hour;
    private final int minute;
    private final long count;

    private static final Gson gson = new Gson();

    public TripHourMinute(int hour, int minute, long count) {
        this.hour = hour;
        this.minute = minute;
        this.count = count;
    }

    @Override
    public String toString() {
        return gson.toJson(this);
    }

}
