package model;

import com.google.gson.Gson;

import java.util.Objects;

public class PopularDestination {

    private static final Gson gson = new Gson();
    public Point locationCenter;
    public long passengers;

    public PopularDestination(Point locationCenter, long passengers) {
        this.locationCenter = locationCenter;
        this.passengers = passengers;
    }

    public Point getLocationCenter() {
        return locationCenter;
    }

    public void setLocationCenter(Point locationCenter) {
        this.locationCenter = locationCenter;
    }

    public long getPassengers() {
        return passengers;
    }

    public void setPassengers(long passengers) {
        this.passengers = passengers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PopularDestination that = (PopularDestination) o;
        return passengers == that.passengers && locationCenter.equals(that.locationCenter);
    }

    @Override
    public int hashCode() {
        return Objects.hash(locationCenter, passengers);
    }

    @Override
    public String toString() {
        return gson.toJson(this);
    }
}
