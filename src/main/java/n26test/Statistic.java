package n26test;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class Statistic {
    private BigDecimal min;
    private BigDecimal max;
    private BigDecimal avg;
    private Integer count;
    private BigDecimal sum;

    @Override
    public String toString() {
        return "Statistic{" +
                "min=" + min +
                ", max=" + max +
                ", avg=" + avg +
                ", count=" + count +
                ", sum=" + sum +
                '}';
    }

    public Statistic() {
        min = new BigDecimal(Double.MAX_VALUE);
        max = new BigDecimal(Double.MIN_VALUE);
        avg = new BigDecimal(0);
        count = 0;
        sum = new BigDecimal(0);
    }

    public BigDecimal getMin() {
        return min;
    }

    public void setMin(BigDecimal min) {
        this.min = min.min(this.min);
    }

    public BigDecimal getMax() {
        return max;
    }

    public void setMax(BigDecimal max) {
        this.max = max.max(this.max);
    }

    public BigDecimal getAvg() {
        return avg;
    }

    public void computeAvg() {
        if (this.count > 0)
            this.avg = this.sum.divide(new BigDecimal(this.count), RoundingMode.HALF_UP);
    }

    public int getCount() {
        return count;
    }

    public void incrementCount() {
        this.count += 1;
    }

    public BigDecimal getSum() {
        return sum;
    }

    public void incrementSum(BigDecimal sum) {
        this.sum = this.sum.add(sum);
    }
}
