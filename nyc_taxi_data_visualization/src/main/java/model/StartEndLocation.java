package model;

import java.util.Objects;

public class StartEndLocation {
    private double startLatitude;
    private double startLongitude;
    private double endLatitude;
    private double endLongitude;

    public StartEndLocation(double startLatitude, double startLongitude, double endLatitude, double endLongitude) {
        this.startLatitude = startLatitude;
        this.startLongitude = startLongitude;
        this.endLatitude = endLatitude;
        this.endLongitude = endLongitude;
    }

    public double getStartLatitude() {
        return startLatitude;
    }

    public void setStartLatitude(double startLatitude) {
        this.startLatitude = startLatitude;
    }

    public double getStartLongitude() {
        return startLongitude;
    }

    public void setStartLongitude(double startLongitude) {
        this.startLongitude = startLongitude;
    }

    public double getEndLatitude() {
        return endLatitude;
    }

    public void setEndLatitude(double endLatitude) {
        this.endLatitude = endLatitude;
    }

    public double getEndLongitude() {
        return endLongitude;
    }

    public void setEndLongitude(double endLongitude) {
        this.endLongitude = endLongitude;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StartEndLocation)) return false;
        StartEndLocation that = (StartEndLocation) o;
        return Double.compare(that.startLatitude, startLatitude) == 0 &&
                Double.compare(that.startLongitude, startLongitude) == 0 &&
                Double.compare(that.endLatitude, endLatitude) == 0 &&
                Double.compare(that.endLongitude, endLongitude) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(startLatitude, startLongitude, endLatitude, endLongitude);
    }
}
