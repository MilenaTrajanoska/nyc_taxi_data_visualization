import model.Point;
import model.PopularDestination;
import model.TaxiRide;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;
import sink.SinkFactory;
import source.TaxiRideEventSource;

import java.time.Duration;
import java.util.Objects;

import static util.GeoUtils.getGridCellCenterPoint;

public class DropOffEventProcessing {

    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.getCheckpointConfig().setCheckpointStorage("file:///src/main/resources/checkpoint");
        env.enableCheckpointing(1000);

        DataStreamSource<TaxiRide> rides = env
                .addSource(new TaxiRideEventSource("src/main/resources/data/full_data.csv", 200))
                .setParallelism(1);

        WatermarkStrategy<TaxiRide> watermarkStrategy =
                WatermarkStrategy.<TaxiRide>forBoundedOutOfOrderness(Duration.ofSeconds(3))
                        .withTimestampAssigner(
                                (ride, streamRecordTimestamp) -> ride.getEventTimeMillis());

        rides.filter(Objects::nonNull)
                .assignTimestampsAndWatermarks(watermarkStrategy)
                .keyBy(tr -> getGridCellCenterPoint(tr.getDropOffLong(), tr.getDropOffLat()))
                .window(TumblingEventTimeWindows.of(Time.seconds(10)))
                .process(new AddPassengers())
                .map(PopularDestination::toString)
                .sinkTo(SinkFactory.getFlinkKafkaStringSink());

        env.execute("Popular destinations");

    }

    public static class AddPassengers extends ProcessWindowFunction <
            TaxiRide, PopularDestination, Point, TimeWindow> {
        @Override
        public void process(Point key, Context context, Iterable<TaxiRide> rides, Collector<PopularDestination> out) {
            long sumOfPassengers = 0;
            for (TaxiRide r : rides) {
                sumOfPassengers += r.getPassengerCnt();
            }
            out.collect(new PopularDestination(key, sumOfPassengers));
            System.out.println("Collected");
        }
    }
}