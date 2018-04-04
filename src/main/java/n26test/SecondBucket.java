package n26test;

import org.joda.time.DateTime;

import java.math.BigDecimal;

import static java.lang.Math.toIntExact;

public class SecondBucket {
    private Statistic statistic;
    private int secondFromEpoch;

    public SecondBucket() {
        statistic = new Statistic();
        secondFromEpoch = 0;
    }

    private void resetIfStale(DateTime newTimestamp) {
        int second = secondFromTimestamp(newTimestamp);
        if (secondFromEpoch != second) {
            secondFromEpoch = second;
            statistic = new Statistic();
        }
    }

    synchronized public void updateBucket(DateTime timestamp, BigDecimal amount) {
        resetIfStale(timestamp);
        statistic.incrementCount();
        statistic.incrementSum(amount);
        statistic.setMax(amount);
        statistic.setMin(amount);
        statistic.computeAvg();
    }

    public Statistic getStatistic(DateTime timestamp) {
        resetIfStale(timestamp);
        return this.statistic;
    }

    private int secondFromTimestamp(DateTime timestamp) {
        return toIntExact(timestamp.getMillis() / 1000);
    }
}

