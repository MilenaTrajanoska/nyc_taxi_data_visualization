package source;

import model.TaxiRide;
import org.apache.flink.streaming.api.functions.source.SourceFunction;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.ZoneOffset;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Random;

public class TaxiRideEventSource implements SourceFunction<TaxiRide> {

    private final long maxDelayMillis;
    private static BufferedReader bufferedReader = null;
    private static final Random randomDelay = new Random(123);
    private static long dataStartTime = 0L;
    private static final Comparator<? super TaxiRide> taxiRideComparator =
            Comparator.comparing(TaxiRide::getEventTimeMillis)
            .thenComparing(TaxiRide::compareTo);

    public TaxiRideEventSource(String dataFilePath, long maxDelayMillis) throws FileNotFoundException {
        this.maxDelayMillis = maxDelayMillis;
        bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(dataFilePath), StandardCharsets.UTF_8));
    }

    @Override
    public void run(SourceContext<TaxiRide> sourceContext) throws Exception {
        PriorityQueue<TaxiRide> emitScheduleRides = new PriorityQueue<>(10000, taxiRideComparator);

        boolean first = true;
        String line;
        long curNextDelayedEventTime = 0L;

        while ((line = bufferedReader.readLine()) != null) {
            try {

                TaxiRide ride = TaxiRide.taxiRideParseFromLine(line);

                if(first) {

                    dataStartTime = ride.getDropOffDate().toInstant(ZoneOffset.UTC).toEpochMilli();
                    curNextDelayedEventTime = dataStartTime;
                    first = false;

                }

                long randDelay = randomDelay.nextInt((int) maxDelayMillis);
                randDelay = Math.min(randDelay, maxDelayMillis) + 100;
                long delayedEventTime = curNextDelayedEventTime + randDelay;
                ride.setEventTimeMillis(delayedEventTime);
                emitScheduleRides.add(ride);
                curNextDelayedEventTime = delayedEventTime;

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        long previousTime = dataStartTime;
        //while (!emitScheduleRides.isEmpty()) {
        for(int i=0; i<=30; i++){
            TaxiRide emittedRide = emitScheduleRides.poll();
            long sleepTime = Math.abs(emittedRide.eventTimeMillis - previousTime);
            previousTime = emittedRide.eventTimeMillis;
//            try {
//                Thread.sleep(sleepTime);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
            sourceContext.collectWithTimestamp(emittedRide, emittedRide.eventTimeMillis);
        }
    }

    @Override
    public void cancel() {
        try {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            bufferedReader = null;
            dataStartTime = 0L;
        }
    }
}
