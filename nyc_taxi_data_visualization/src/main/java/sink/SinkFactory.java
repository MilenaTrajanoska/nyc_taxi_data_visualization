package sink;

import model.PopularDestination;
import model.TripCount;
import model.TripDuration;
import org.apache.flink.connector.kafka.sink.KafkaSink;

import java.io.IOException;


public class SinkFactory {

    private static KafkaSink<PopularDestination> popularDestinationFlinkKafkaProducer;
    private static KafkaSink<String> stringKafkaProducer;
    private static KafkaSink<TripDuration> tripDurationKafkaProducer;
    private static KafkaSink<TripCount> tripCountKafkaProducer;


    private SinkFactory() {}

    public static synchronized KafkaSink<PopularDestination> getFlinkKafkaPopularDestinationsSink() throws IOException {
        if (popularDestinationFlinkKafkaProducer == null) {
            popularDestinationFlinkKafkaProducer = KafkaSinkPopularDestinations.build();
        }
        return popularDestinationFlinkKafkaProducer;
    }

    public static synchronized KafkaSink<String> getFlinkKafkaStringSink() {
        if (stringKafkaProducer == null) {
            stringKafkaProducer = KafkaSinkString.build();
        }
        return stringKafkaProducer;
    }

    public static synchronized KafkaSink<TripDuration> getFlinkKafkaTripDurationSink() {
        if (tripDurationKafkaProducer == null) {
            tripDurationKafkaProducer = KafkaSinkTripDuration.build();
        }
        return tripDurationKafkaProducer;
    }

    public static synchronized KafkaSink<TripCount> getFlinkKafkaTripCountSink() {
        if (tripCountKafkaProducer == null) {
            tripCountKafkaProducer = KafkaSinkTripCount.build();
        }
        return tripCountKafkaProducer;
    }
    
}
