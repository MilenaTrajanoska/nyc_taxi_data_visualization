package sink;

import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.connector.base.DeliveryGuarantee;
import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchema;
import org.apache.flink.connector.kafka.sink.KafkaSink;
import org.apache.kafka.common.protocol.types.Field;

import java.util.Properties;

public class KafkaSinkString {

    public static KafkaSink<String> build() {
        Properties props = PropertiesFactory.getProducerKafkaProperties();

        return KafkaSink.<String>builder()
                .setBootstrapServers(props.getProperty("bootstrap.servers"))
                .setKafkaProducerConfig(props)
                .setRecordSerializer(KafkaRecordSerializationSchema.builder()
                        .setTopic(props.getProperty("topic.popular-destinations"))
                        .setValueSerializationSchema(new SimpleStringSchema())
                        .build()
                )
                .setDeliverGuarantee(DeliveryGuarantee.AT_LEAST_ONCE)
                .build();
    }
}
