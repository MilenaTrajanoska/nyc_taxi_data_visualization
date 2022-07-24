import aggreagations.AddPassengers;
import aggreagations.AverageDuration;
import aggreagations.CountRidesPerMinute;
import aggreagations.CountTrips;
import model.HourMinuteObject;
import model.Point;
import model.StartEndLocation;
import model.TaxiRide;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.SlidingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import sink.SinkFactory;
import source.TaxiRideEventSource;

import java.time.Duration;
import java.util.Objects;

import static util.GeoUtils.getGridCellCenterPoint;
import static util.GeoUtils.getGridSegmentCenterPoint;

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
                .windowAll(SlidingProcessingTimeWindows.of(Time.minutes(1), Time.minutes(1)))
                .process(new AverageDuration())
                .sinkTo(SinkFactory.getFlinkKafkaTripDurationSink())
                .setParallelism(5);

//        rides.filter(Objects::nonNull)
//                .assignTimestampsAndWatermarks(watermarkStrategy)
//                .keyBy(tr -> {
//                    Point startPoint = getGridSegmentCenterPoint(tr.getPickUpLong(), tr.getPickUpLat());
//                    Point endPoint = getGridSegmentCenterPoint(tr.getDropOffLong(), tr.getDropOffLat());
//                    return new StartEndLocation(
//                            startPoint.getLatitude(),
//                            startPoint.getLongitude(),
//                            endPoint.getLatitude(),
//                            endPoint.getLongitude()
//                    );
//                })
//                .window(SlidingProcessingTimeWindows.of(Time.minutes(10), Time.seconds(60)))
//                .process(new CountTrips())
//                .sinkTo(SinkFactory.getFlinkKafkaTripCountSink())
//                .setParallelism(5);

        rides.filter(Objects::nonNull)
                .assignTimestampsAndWatermarks(watermarkStrategy)
                .keyBy(tr -> new HourMinuteObject(tr.getPickUpDate().getHour(), tr.getPickUpDate().getMinute()))
                .window(SlidingProcessingTimeWindows.of(Time.minutes(10), Time.seconds(60)))
                .process(new CountRidesPerMinute())
                .sinkTo(SinkFactory.getFlinkKafkaTripHourMinuteSink())
                .setParallelism(5);

        env.execute("Popular destinations");

    }
}