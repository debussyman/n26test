package n26test;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.Math.toIntExact;

@RestController
public class TransactionsController {

    public static final int SECONDS_WINDOW = 60;
    private List<SecondBucket> buckets;
    private final Logger logger = LoggerFactory.getLogger(TransactionsController.class);

    public TransactionsController() {
        super();
        Supplier<SecondBucket> supplier = SecondBucket::new;
        buckets = Stream
                .generate(supplier)
                .limit(2 * SECONDS_WINDOW - 1)
                .collect(Collectors.toList());
    }

    @RequestMapping("/")
    public String index() {
        return "Greetings from Spring Boot!";
    }

    @RequestMapping(value = "/transactions", method = RequestMethod.POST)
    public void postTransaction(@RequestBody final Transaction transaction, HttpServletResponse response) {
        DateTime transactionTime = new DateTime(transaction.timestamp);

        if (transactionTime.isAfter(DateTime.now().minusSeconds(SECONDS_WINDOW))) {
            for (int i=0; i<60; i++) {
                SecondBucket bucket = bucketForTimestamp(transactionTime.plusSeconds(i));
                bucket.updateBucket(transactionTime, transaction.amount);
            }
            response.setStatus(HttpServletResponse.SC_CREATED);
        } else {
            logger.info("transaction is more than 60 seconds in the past - {}", transactionTime);
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
        }
    }

    @RequestMapping(value = "/statistics", method = RequestMethod.GET)
    public Statistic getStatistics() {
        DateTime now = DateTime.now();
        SecondBucket bucket = bucketForTimestamp(now);
        logger.info("statistics : {} ", bucket.getStatistic(now));
        return bucket.getStatistic(now);
    }

    private SecondBucket bucketForTimestamp(DateTime timestamp) {
        int offset = toIntExact((timestamp.getMillis() / 1000)) % buckets.size();
        return buckets.get(offset);
    }

}
