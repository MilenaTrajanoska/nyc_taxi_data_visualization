package aggreagations;

import org.apache.flink.streaming.api.functions.windowing.ProcessAllWindowFunction;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

public class AverageDuration extends ProcessAllWindowFunction<Long, Long, TimeWindow> {

    @Override
    public void process(Context context, Iterable<Long> iterable, Collector<Long> collector) throws Exception {
        long sum = 0;
        long count = 0;
        for(Long i : iterable){
            sum += i;
            count++;
        }
        long average = 0;

        if (count != 0) {
            average = sum / count;
        }

        collector.collect(average);
    }
}

