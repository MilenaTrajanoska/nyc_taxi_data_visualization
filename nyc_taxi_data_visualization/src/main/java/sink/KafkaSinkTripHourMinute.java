package sink;

import model.TripDuration;
import model.TripHourMinute;
import org.apache.flink.api.common.serialization.SerializationSchema;
import org.apache.flink.connector.base.DeliveryGuarantee;
import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchema;
import org.apache.flink.connector.kafka.sink.KafkaSink;
import serialization.TripHourMinuteSerializationSchema;

import java.util.Properties;

public class KafkaSinkTripHourMinute {
    private static final SerializationSchema<TripHourMinute> tripHourMinuteSerializationSchema =
            new TripHourMinuteSerializationSchema();

    public static KafkaSink<TripHourMinute> build() {
        Properties props = PropertiesFactory.getProducerKafkaProperties();

        return KafkaSink.<TripHourMinute>builder()
                .setBootstrapServers(props.getProperty("bootstrap.servers"))
                .setKafkaProducerConfig(props)
                .setRecordSerializer(KafkaRecordSerializationSchema.builder()
                        .setTopic(props.getProperty("topic.trip-hour-minute"))
                        .setValueSerializationSchema(tripHourMinuteSerializationSchema)
                        .build()
                )
                .setDeliverGuarantee(DeliveryGuarantee.AT_LEAST_ONCE)
                .build();
    }
}
