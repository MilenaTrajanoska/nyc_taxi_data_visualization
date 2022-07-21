package serialization;

import model.PopularDestination;
import org.apache.flink.api.common.serialization.SerializationSchema;

import java.nio.charset.StandardCharsets;

public class PopularDestinationsSerializationSchema implements SerializationSchema<PopularDestination> {

    @Override
    public byte[] serialize(PopularDestination popularDestination) {
        return popularDestination.toString().getBytes(StandardCharsets.UTF_8);
    }
}