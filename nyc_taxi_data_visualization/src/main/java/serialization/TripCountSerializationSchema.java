package serialization;

import model.TripCount;
import org.apache.flink.api.common.serialization.SerializationSchema;

import java.nio.charset.StandardCharsets;

public class TripCountSerializationSchema implements SerializationSchema<TripCount> {
    @Override
    public byte[] serialize(TripCount tripCount) {
        return tripCount.toString().getBytes(StandardCharsets.UTF_8);
    }
}
