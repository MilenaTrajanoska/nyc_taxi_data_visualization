package model;

import exception.TaxiRideFormatException;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;

public class TaxiRide implements Comparable<TaxiRide>, Serializable {

    public String rideId;
    public String vendorId;
    public LocalDateTime pickUpDate;
    public LocalDateTime dropOffDate;
    public int passengerCnt;
    public float pickUpLong;
    public float pickUpLat;
    public float dropOffLong;
    public float dropOffLat;
    public String storeAndForward;
    public long tripDuration;
    public long eventTimeMillis;

    private static final Comparator<TaxiRide> taxiRideComparator = Comparator.comparing(TaxiRide::getRideId)
            .thenComparing(TaxiRide::getEventTimeMillis)
            .thenComparing(TaxiRide::getPickUpDate)
            .thenComparing(TaxiRide::getDropOffDate)
            .thenComparing(TaxiRide::getVendorId)
            .thenComparing(TaxiRide::getPickUpLong)
            .thenComparing(TaxiRide::getPickUpLat)
            .thenComparing(TaxiRide::getDropOffLong)
            .thenComparing(TaxiRide::getDropOffLat)
            .thenComparing(TaxiRide::getPassengerCnt)
            .thenComparing(TaxiRide::getTripDuration)
            .thenComparing(TaxiRide::getStoreAndForward);

    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public TaxiRide(
            String  rideId,
            String vendorId,
            LocalDateTime pickUpDate,
            LocalDateTime dropOffDate,
            int passengerCnt,
            float pickUpLong,
            float pickUpLat,
            float dropOffLong,
            float dropOffLat,
            String storeAndForward,
            long tripDuration
    ) {

        this.rideId = rideId;
        this.vendorId = vendorId;
        this.pickUpDate = pickUpDate;
        this.dropOffDate = dropOffDate;
        this.passengerCnt = passengerCnt;
        this.pickUpLong = pickUpLong;
        this.pickUpLat = pickUpLat;
        this.dropOffLong = dropOffLong;
        this.dropOffLat = dropOffLat;
        this.storeAndForward = storeAndForward;
        this.tripDuration = tripDuration;
    }


    public Long getEventTimeMillis() {
        return eventTimeMillis;
    }

    public void setEventTimeMillis(long eventTimeMillis) {
        this.eventTimeMillis = eventTimeMillis;
    }

    public String getRideId() {
        return rideId;
    }

    public String getVendorId() {
        return vendorId;
    }

    public LocalDateTime getPickUpDate() {
        return pickUpDate;
    }

    public LocalDateTime getDropOffDate() {
        return dropOffDate;
    }

    public int getPassengerCnt() {
        return passengerCnt;
    }

    public float getPickUpLong() {
        return pickUpLong;
    }

    public float getPickUpLat() {
        return pickUpLat;
    }

    public float getDropOffLong() {
        return dropOffLong;
    }

    public float getDropOffLat() {
        return dropOffLat;
    }

    public String getStoreAndForward() {
        return storeAndForward;
    }

    public long getTripDuration() {
        return tripDuration;
    }

    public static TaxiRide taxiRideParseFromLine(String line) throws TaxiRideFormatException {
        if (line == null) {
            throw new NullPointerException();
        }

        String[] parts = line.split(",");
        if (parts.length != 11) {
            throw new TaxiRideFormatException();
        }

        String rideId = parts[0];
        String vendorId = parts[1];
        LocalDateTime pickUpDate = LocalDateTime.parse(parts[2], dateFormatter);
        LocalDateTime dropOffDate = LocalDateTime.parse(parts[3], dateFormatter);
        int passengerCnt = Integer.parseInt(parts[4]);
        float pickUpLong = Float.parseFloat(parts[5]);
        float pickUpLat = Float.parseFloat(parts[6]);
        float dropOffLong = Float.parseFloat(parts[7]);
        float dropOffLat = Float.parseFloat(parts[8]);
        String storeAndForward = parts[9];
        long tripDuration = Long.parseLong(parts[10]);

        return new TaxiRide(
                rideId,
                vendorId,
                pickUpDate,
                dropOffDate,
                passengerCnt,
                pickUpLong,
                pickUpLat,
                dropOffLong,
                dropOffLat,
                storeAndForward,
                tripDuration
        );
    }

    @Override
    public String toString() {
        return "TaxiRide{" +
                "rideId=" + rideId +
                ", eventTime=" + eventTimeMillis +
                ", vendorId=" + vendorId +
                ", pickUpDate=" + pickUpDate +
                ", dropOffDate=" + dropOffDate +
                ", passengerCnt=" + passengerCnt +
                ", pickUpLong=" + pickUpLong +
                ", pickUpLat=" + pickUpLat +
                ", dropOffLong=" + dropOffLong +
                ", dropOffLat=" + dropOffLat +
                ", storeAndForward='" + storeAndForward + '\'' +
                ", tripDuration=" + tripDuration +
                '}';
    }

    public int compareTo (@Nullable TaxiRide other) {
        if(other == null) {
            return 1;
        }
        if (this.equals(other)) {
            return 0;
        }

        return taxiRideComparator.compare(this, other);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TaxiRide)) return false;
        TaxiRide taxiRide = (TaxiRide) o;
        return rideId.equals(taxiRide.rideId) &&
                eventTimeMillis == taxiRide.eventTimeMillis &&
                vendorId.equals(taxiRide.vendorId) &&
                passengerCnt == taxiRide.passengerCnt &&
                Float.compare(taxiRide.pickUpLong, pickUpLong) == 0 &&
                Float.compare(taxiRide.pickUpLat, pickUpLat) == 0 &&
                Float.compare(taxiRide.dropOffLong, dropOffLong) == 0 &&
                Float.compare(taxiRide.dropOffLat, dropOffLat) == 0 &&
                tripDuration == taxiRide.tripDuration &&
                pickUpDate.compareTo(taxiRide.pickUpDate) == 0 &&
                dropOffDate.compareTo(taxiRide.dropOffDate) == 0 &&
                storeAndForward.compareTo(taxiRide.storeAndForward) == 0;
    }

    @Override
    public int hashCode() {
        return rideId.hashCode() *
                Long.hashCode(eventTimeMillis) *
                vendorId.hashCode() *
                pickUpDate.hashCode() *
                dropOffDate.hashCode() *
                Integer.hashCode(passengerCnt) *
                Float.hashCode(pickUpLong) *
                Float.hashCode(pickUpLat) *
                Float.hashCode(dropOffLong) *
                Float.hashCode(dropOffLat) *
                storeAndForward.hashCode() *
                Long.hashCode(tripDuration);
    }
}