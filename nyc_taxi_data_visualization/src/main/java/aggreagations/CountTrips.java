package aggreagations;

import model.StartEndLocation;
import model.TaxiRide;
import model.TripCount;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

public class CountTrips extends ProcessWindowFunction<TaxiRide, TripCount, StartEndLocation, TimeWindow> {

    @Override
    public void process(StartEndLocation startEndLocation, Context context, Iterable<TaxiRide> iterable, Collector<TripCount> collector) throws Exception {
        long tripCount = 0;
        for (TaxiRide r : iterable) {
            tripCount ++;
        }
        collector.collect(new TripCount(
                startEndLocation.getStartLatitude(),
                startEndLocation.getStartLongitude(),
                startEndLocation.getEndLatitude(),
                startEndLocation.getEndLongitude(),
                tripCount
        ));
    }

}
