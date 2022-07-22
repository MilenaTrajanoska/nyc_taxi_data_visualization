package aggreagations;

import model.TaxiRide;
import model.TripDuration;
import org.apache.flink.streaming.api.functions.windowing.ProcessAllWindowFunction;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

import java.time.LocalDateTime;

public class AverageDuration extends ProcessAllWindowFunction<TaxiRide, TripDuration, TimeWindow> {

    @Override
    public void process(Context context, Iterable<TaxiRide> iterable, Collector<TripDuration> collector) throws Exception {
        long sum = 0;
        long count = 0;
        long km_passed = 0;

        for(TaxiRide r : iterable){
            sum += r.getTripDuration();
            km_passed += calculateHaversineDistance(r.getPickUpLat(), r.getPickUpLong(), r.getDropOffLat(), r.getDropOffLong());
            count++;
        }

        TripDuration tripDuration = new TripDuration(sum / 60, count, km_passed, LocalDateTime.now());
        collector.collect(tripDuration);
    }

    private double calculateHaversineDistance(double startLatitude, double startLongitude, double endLatitude, double endLongitude) {
        double lon1 = Math.toRadians(startLongitude);
        double lon2 = Math.toRadians(endLongitude);
        double lat1 = Math.toRadians(startLatitude);
        double lat2 = Math.toRadians(endLatitude);

        // Haversine formula
        double dlon = lon2 - lon1;
        double dlat = lat2 - lat1;
        double a = Math.pow(Math.sin(dlat / 2), 2)
                + Math.cos(lat1) * Math.cos(lat2)
                * Math.pow(Math.sin(dlon / 2),2);

        double c = 2 * Math.asin(Math.sqrt(a));

        // Radius of earth in kilometers. Use 3956
        // for miles
        double r = 6371;

        // calculate the result
        return c * r;
    }
}

