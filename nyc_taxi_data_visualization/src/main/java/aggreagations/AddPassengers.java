package aggreagations;

import model.Point;
import model.PopularDestination;
import model.TaxiRide;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

import java.util.Optional;
import java.util.stream.StreamSupport;

public class AddPassengers extends ProcessWindowFunction<TaxiRide, PopularDestination, Point, TimeWindow> {
        @Override
        public void process(Point key, Context context, Iterable<TaxiRide> rides, Collector<PopularDestination> out) {
           Optional<Integer> sumOfPassengers = StreamSupport.stream(rides.spliterator(), false)
                    .map(TaxiRide::getPassengerCnt)
                    .reduce(Integer::sum);
           
           long passengers = 0L;
           if (sumOfPassengers.isPresent()) {
               passengers = sumOfPassengers.get();
           }
            out.collect(new PopularDestination(key, passengers));
        }
}
