package sink;

import model.TripDuration;
import org.apache.flink.api.common.serialization.SerializationSchema;
import org.apache.flink.connector.base.DeliveryGuarantee;
import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchema;
import org.apache.flink.connector.kafka.sink.KafkaSink;
import serialization.TripDurationSerializationSchema;

import java.util.Properties;

public class KafkaSinkTripDuration {
    private static final SerializationSchema<TripDuration> tripDurationKafkaSerializationSchema =
            new TripDurationSerializationSchema();

    public static KafkaSink<TripDuration> build() {
        Properties props = PropertiesFactory.getProducerKafkaProperties();

        return KafkaSink.<TripDuration>builder()
                .setBootstrapServers(props.getProperty("bootstrap.servers"))
                .setKafkaProducerConfig(props)
                .setRecordSerializer(KafkaRecordSerializationSchema.builder()
                        .setTopic(props.getProperty("topic.trip-durations"))
                        .setValueSerializationSchema(tripDurationKafkaSerializationSchema)
                        .build()
                )
                .setDeliverGuarantee(DeliveryGuarantee.AT_LEAST_ONCE)
                .build();
    }
}
