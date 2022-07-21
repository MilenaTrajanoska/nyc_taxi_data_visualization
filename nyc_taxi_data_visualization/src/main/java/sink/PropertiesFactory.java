package sink;

import java.util.Properties;

public class PropertiesFactory {
    public static Properties getProducerKafkaProperties() {
        Properties props = new Properties();
        //props.load(new FileInputStream("src/main/resources/application.properties"));
        props.put("bootstrap.servers", "tricycle-01.srvs.cloudkafka.com:9094");
        props.put("sasl.mechanism", "SCRAM-SHA-256");
        props.put("sasl.jaas.config", "org.apache.kafka.common.security.scram.ScramLoginModule required username=\"uwgbzh37\" password=\"O4n_4-ui5DDzfORdAbqNBlYhl7gwJdd7\";");
        props.put("topic.popular-destinations", "uwgbzh37-popular-destinations");
        props.put("topic.trip-durations", "uwgbzh37-trip-durations");
        props.put("transaction.timeout.ms", 6000000);
        props.put("retries", 5);
        props.put("security.protocol", "SASL_SSL");
        return props;
    }
}
