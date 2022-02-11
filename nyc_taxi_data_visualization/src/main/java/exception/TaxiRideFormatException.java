package exception;

public class TaxiRideFormatException extends Exception {

    private static final String message = "Invalid format for parsing TaxiRide object!";

    public TaxiRideFormatException() {
        super(message);
    }
}
