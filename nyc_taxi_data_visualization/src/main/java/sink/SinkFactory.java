package sink;

import model.PopularDestination;
import org.apache.flink.api.common.serialization.SerializationSchema;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.connector.base.DeliveryGuarantee;
import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchema;
import org.apache.flink.connector.kafka.sink.KafkaSink;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Properties;


public class SinkFactory {

    private static KafkaSink<PopularDestination> popularDestinationFlinkKafkaProducer;
    private static KafkaSink<String> stringKafkaProducer;

    private SinkFactory() {}
    private static class PopularDestinationsSerializationSchema implements SerializationSchema<PopularDestination> {

        @Override
        public byte[] serialize(PopularDestination popularDestination) {
            return popularDestination.toString().getBytes(StandardCharsets.UTF_8);
        }
    }
    private static final SerializationSchema<PopularDestination> popularDestinationKafkaSerializationSchema = new PopularDestinationsSerializationSchema();

    public static synchronized KafkaSink<PopularDestination> getFlinkKafkaPopularDestinationsSink() throws IOException {
        if (popularDestinationFlinkKafkaProducer == null) {
            Properties props = getProducerKafkaProperties();

            popularDestinationFlinkKafkaProducer = KafkaSink.<PopularDestination>builder()
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
        return popularDestinationFlinkKafkaProducer;
    }

    public static synchronized KafkaSink<String> getFlinkKafkaStringSink() {
        if (stringKafkaProducer == null) {
            Properties props = getProducerKafkaProperties();

            stringKafkaProducer = KafkaSink.<String>builder()
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
        return stringKafkaProducer;
    }

    private static Properties getProducerKafkaProperties() {
        Properties props = new Properties();
        //props.load(new FileInputStream("src/main/resources/application.properties"));
        props.put("bootstrap.servers", "tricycle-01.srvs.cloudkafka.com:9094");
        props.put("sasl.mechanism", "SCRAM-SHA-256");
        props.put("sasl.jaas.config", "org.apache.kafka.common.security.scram.ScramLoginModule required username=\"uwgbzh37\" password=\"O4n_4-ui5DDzfORdAbqNBlYhl7gwJdd7\";");
        props.put("topic.popular-destinations", "uwgbzh37-popular-destinations");
        props.put("transaction.timeout.ms", 6000000);
        props.put("retries", 5);
        props.put("security.protocol", "SASL_SSL");
        return props;
    }
}
