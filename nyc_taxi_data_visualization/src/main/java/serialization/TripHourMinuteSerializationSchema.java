package serialization;

import model.TripHourMinute;
import org.apache.flink.api.common.serialization.SerializationSchema;

import java.nio.charset.StandardCharsets;

public class TripHourMinuteSerializationSchema implements SerializationSchema<TripHourMinute> {
    @Override
    public byte[] serialize(TripHourMinute tripHourMinute) {
        return tripHourMinute.toString().getBytes(StandardCharsets.UTF_8);
    }
}