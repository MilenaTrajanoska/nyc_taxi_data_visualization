package serialization;

import model.TripDuration;
import org.apache.flink.api.common.serialization.SerializationSchema;

import java.nio.charset.StandardCharsets;

public class TripDurationSerializationSchema implements SerializationSchema<TripDuration> {
    @Override
    public byte[] serialize(TripDuration tripDuration) {
        return tripDuration.toString().getBytes(StandardCharsets.UTF_8);
    }
}
