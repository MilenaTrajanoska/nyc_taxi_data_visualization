package model;

import java.util.Objects;

public class HourMinuteObject {
    private final int hour;
    private final int minute;

    public HourMinuteObject(int hour, int minute) {
        this.hour = hour;
        this.minute = minute;
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HourMinuteObject)) return false;
        HourMinuteObject that = (HourMinuteObject) o;
        return hour == that.hour &&
                minute == that.minute;
    }

    @Override
    public int hashCode() {
        return Objects.hash(hour, minute);
    }

    @Override
    public String toString() {
        return "HourMinuteObject{" +
                "hour=" + hour +
                ", minute=" + minute +
                '}';
    }
}
