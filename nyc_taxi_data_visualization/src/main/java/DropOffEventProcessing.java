import aggreagations.AddPassengers;
import aggreagations.AverageDuration;
import model.TaxiRide;
import model.TripDuration;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.SlidingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import sink.SinkFactory;
import source.TaxiRideEventSource;

import java.sql.Date;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

import static util.GeoUtils.getGridCellCenterPoint;

public class DropOffEventProcessing {

    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.getCheckpointConfig().setCheckpointStorage("file:///src/main/resources/checkpoint");
        env.enableCheckpointing(180000);

        DataStreamSource<TaxiRide> rides = env
                .addSource(new TaxiRideEventSource("src/main/resources/data/full_data.csv", 200))
                .setParallelism(1);

        WatermarkStrategy<TaxiRide> watermarkStrategy =
                WatermarkStrategy.<TaxiRide>forBoundedOutOfOrderness(Duration.ofMinutes(1))
                        .withTimestampAssigner(
                                (ride, streamRecordTimestamp) -> ride.getEventTimeMillis());

        rides.filter(Objects::nonNull)
                .assignTimestampsAndWatermarks(watermarkStrategy)
                .keyBy(tr -> getGridCellCenterPoint(tr.getDropOffLong(), tr.getDropOffLat()))
                .window(SlidingProcessingTimeWindows.of(Time.minutes(10), Time.seconds(60)))
                .process(new AddPassengers())
                .sinkTo(SinkFactory.getFlinkKafkaPopularDestinationsSink())
                .setParallelism(5);

        rides.filter(Objects::nonNull)
                .assignTimestampsAndWatermarks(watermarkStrategy)
                .map(TaxiRide::getTripDuration)
                .windowAll(SlidingProcessingTimeWindows.of(Time.hours(1), Time.hours(1)))
                .process(new AverageDuration())
                .map(duration -> new TripDuration(duration, LocalDateTime.now()))
                .sinkTo(SinkFactory.getFlinkKafkaTripDurationSink())
                .setParallelism(5);

        env.execute("Popular destinations");

    }
}