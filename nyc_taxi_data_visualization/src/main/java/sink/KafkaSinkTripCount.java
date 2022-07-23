package sink;

import model.PopularDestination;
import model.TripCount;
import org.apache.flink.api.common.serialization.SerializationSchema;
import org.apache.flink.connector.base.DeliveryGuarantee;
import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchema;
import org.apache.flink.connector.kafka.sink.KafkaSink;
import serialization.TripCountSerializationSchema;

import java.util.Properties;

public class KafkaSinkTripCount {
    private static final SerializationSchema<TripCount> tripCountKafkaSerializationSchema =
            new TripCountSerializationSchema();

    public static KafkaSink<TripCount> build() {
        Properties props = PropertiesFactory.getProducerKafkaProperties();

        return KafkaSink.<TripCount>builder()
                .setBootstrapServers(props.getProperty("bootstrap.servers"))
                .setKafkaProducerConfig(props)
                .setRecordSerializer(KafkaRecordSerializationSchema.builder()
                        .setTopic(props.getProperty("topic.trip-count"))
                        .setValueSerializationSchema(tripCountKafkaSerializationSchema)
                        .build()
                )
                .setDeliverGuarantee(DeliveryGuarantee.AT_LEAST_ONCE)
                .build();
    }
}
