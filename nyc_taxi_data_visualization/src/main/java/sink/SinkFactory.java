package sink;

import model.Point;
import model.PopularDestination;
import org.apache.flink.api.common.serialization.SerializationSchema;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
//import org.apache.flink.connector.base.DeliveryGuarantee;
//import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchema;
//import org.apache.flink.connector.kafka.sink.KafkaSink;
import org.apache.flink.connector.base.DeliveryGuarantee;
import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchema;
import org.apache.flink.connector.kafka.sink.KafkaSink;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
import org.apache.flink.streaming.connectors.kafka.KafkaSerializationSchema;
import org.apache.flink.streaming.connectors.kafka.partitioner.FlinkFixedPartitioner;
import org.apache.kafka.clients.producer.ProducerRecord;

import javax.annotation.Nullable;
import java.io.FileInputStream;
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
//    static KafkaSerializationSchema<String> serializationSchema = new KafkaSerializationSchema<String>() {
//        @Override
//        public ProducerRecord<byte[], byte[]> serialize(String element, @Nullable Long timestamp) {
//            return new ProducerRecord<>(
//                    "uwgbzh37-popular-destinations", // target topic
//                    element.getBytes(StandardCharsets.UTF_8)); // record contents
//        }
//    };

//    public static FlinkKafkaProducer<String> getKafkaSinkOld() {
//        Properties props = new Properties();
//        props.put("bootstrap.servers", "tricycle-03.srvs.cloudkafka.com:9094");
//        props.put("sasl.mechanism", "PLAIN");
//        props.put("sasl.jaas.config", "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"uwgbzh37\" password=\"O4n_4-ui5DDzfORdAbqNBlYhl7gwJdd7\"");
//        props.put("topic.popular-destinations", "uwgbzh37-popular-destinations");
//
//        return new FlinkKafkaProducer<>(
//                "popular-destinations",             // target topic
//                serializationSchema,    // serialization schema
//                props,             // producer config
//                FlinkKafkaProducer.Semantic.AT_LEAST_ONCE);
//    }

    public static synchronized KafkaSink<PopularDestination> getFlinkKafkaSink() throws IOException {
        if (popularDestinationFlinkKafkaProducer == null) {
            Properties props = new Properties();
            //props.load(new FileInputStream("src/main/resources/application.properties"));
            props.put("bootstrap.servers", "tricycle-03.srvs.cloudkafka.com:9094");
            props.put("sasl.mechanism", "PLAIN");
            props.put("sasl.jaas.config", "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"uwgbzh37\" password=\"O4n_4-ui5DDzfORdAbqNBlYhl7gwJdd7\"");
            props.put("topic.popular-destinations", "uwgbzh37-popular-destinations");

            popularDestinationFlinkKafkaProducer = KafkaSink.<PopularDestination>builder()
                    .setBootstrapServers(props.getProperty("bootstrap.servers"))
                    .setKafkaProducerConfig(props)
                    .setRecordSerializer(KafkaRecordSerializationSchema.builder()
                            .setTopic(props.getProperty("topic.popular-destinations"))
                            .setValueSerializationSchema(popularDestinationKafkaSerializationSchema)
                            .setPartitioner(new FlinkFixedPartitioner<PopularDestination>())
                            .build()
                    )
                    .setDeliverGuarantee(DeliveryGuarantee.AT_LEAST_ONCE)
                    .build();
        }
        return popularDestinationFlinkKafkaProducer;
    }

    public static synchronized KafkaSink<String> getFlinkKafkaStringSink() {
        if (stringKafkaProducer == null) {
            Properties props = new Properties();
            //props.load(new FileInputStream("src/main/resources/application.properties"));
            //props.put("bootstrap.servers", "localhost:9092");
            props.put("bootstrap.servers", "tricycle-01.srvs.cloudkafka.com:9094");
            props.put("sasl.mechanism", "scram-sha-256");
            props.put("sasl.jaas.config", "org.apache.kafka.common.security.scram.ScramLoginModule required username=\"uwgbzh37\" password=\"O4n_4-ui5DDzfORdAbqNBlYhl7gwJdd7\"");
            props.put("topic.popular-destinations", "uwgbzh37-popular-destinations");

            stringKafkaProducer = KafkaSink.<String>builder()
                    .setBootstrapServers(props.getProperty("bootstrap.servers"))
                    .setKafkaProducerConfig(props)
                    .setRecordSerializer(KafkaRecordSerializationSchema.builder()
                            .setTopic(props.getProperty("topic.popular-destinations"))
                            .setValueSerializationSchema(new SimpleStringSchema())
                            //.setPartitioner(new FlinkFixedPartitioner<String>())
                            .build()
                    )
                    .setDeliverGuarantee(DeliveryGuarantee.AT_LEAST_ONCE)
                    .build();
        }
        return stringKafkaProducer;
    }
}
