package n26test;

import org.joda.time.DateTime;

import java.math.BigDecimal;

public class SecondBucket {
    private Statistic statistic;
    private DateTime timestamp;

    public SecondBucket() {
        statistic = new Statistic();
        timestamp = new DateTime(0);
    }

    private void resetIfStale(DateTime newTimestamp) {
        if (timestamp.isBefore(DateTime.now().minusSeconds(TransactionsController.SECONDS_WINDOW))) {
            timestamp = newTimestamp;
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
}

