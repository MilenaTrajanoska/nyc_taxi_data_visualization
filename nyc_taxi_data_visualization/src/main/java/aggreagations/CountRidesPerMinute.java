package aggreagations;

import model.HourMinuteObject;
import model.TaxiRide;
import model.TripHourMinute;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

import java.util.stream.StreamSupport;

public class CountRidesPerMinute extends ProcessWindowFunction<TaxiRide, TripHourMinute, HourMinuteObject, TimeWindow> {

    @Override
    public void process(HourMinuteObject hourMinuteObject, Context context, Iterable<TaxiRide> iterable, Collector<TripHourMinute> collector) throws Exception {

        long tripCount = StreamSupport
                .stream(iterable.spliterator(), false)
                .count();

        collector.collect(new TripHourMinute(
                hourMinuteObject.getHour(),
                hourMinuteObject.getMinute(),
                tripCount
        ));
    }
}
