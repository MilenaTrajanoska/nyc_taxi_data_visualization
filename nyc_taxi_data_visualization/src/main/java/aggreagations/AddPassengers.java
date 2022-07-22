package aggreagations;

import model.Point;
import model.PopularDestination;
import model.TaxiRide;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

public class AddPassengers extends ProcessWindowFunction<TaxiRide, PopularDestination, Point, TimeWindow> {
        @Override
        public void process(Point key, Context context, Iterable<TaxiRide> rides, Collector<PopularDestination> out) {
            long sumOfPassengers = 0;
            for (TaxiRide r : rides) {
                sumOfPassengers += r.getPassengerCnt();
            }
            out.collect(new PopularDestination(key, sumOfPassengers));
        }
}
