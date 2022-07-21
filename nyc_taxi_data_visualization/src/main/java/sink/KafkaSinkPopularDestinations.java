package sink;

import model.PopularDestination;
import org.apache.flink.api.common.serialization.SerializationSchema;
import org.apache.flink.connector.base.DeliveryGuarantee;
import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchema;
import org.apache.flink.connector.kafka.sink.KafkaSink;
import serialization.PopularDestinationsSerializationSchema;

import java.util.Properties;

public class KafkaSinkPopularDestinations {

    private static final SerializationSchema<PopularDestination> popularDestinationKafkaSerializationSchema =
            new PopularDestinationsSerializationSchema();

    public static KafkaSink<PopularDestination> build() {
        Properties props = PropertiesFactory.getProducerKafkaProperties();

        return KafkaSink.<PopularDestination>builder()
                .setBootstrapServers(props.getProperty("bootstrap.servers"))
                .setKafkaProducerConfig(props)
                .setRecordSerializer(KafkaRecordSerializationSchema.builder()
                        .setTopic(props.getProperty("topic.popular-destinations"))
                        .setValueSerializationSchema(popularDestinationKafkaSerializationSchema)
                        .build()
                )
                .setDeliverGuarantee(DeliveryGuarantee.AT_LEAST_ONCE)
                .build();
    }
}
