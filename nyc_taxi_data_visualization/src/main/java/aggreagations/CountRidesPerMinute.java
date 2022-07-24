package aggreagations;

import model.TaxiRide;
import model.TripHourMinute;
import org.apache.flink.streaming.api.functions.windowing.ProcessAllWindowFunction;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

import java.time.LocalDateTime;
import java.util.stream.StreamSupport;

public class CountRidesPerMinute extends ProcessAllWindowFunction<TaxiRide, TripHourMinute, TimeWindow> {

    @Override
    public void process(Context context, Iterable<TaxiRide> iterable, Collector<TripHourMinute> collector) throws Exception {
        long tripCount = StreamSupport
                .stream(iterable.spliterator(), false)
                .count();

        collector.collect(new TripHourMinute(
                LocalDateTime.now().getHour(),
                LocalDateTime.now().getMinute(),
                tripCount
        ));
    }
}
