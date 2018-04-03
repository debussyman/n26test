package n26test;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class TransactionsControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void getHello() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("Greetings from Spring Boot!")));
    }

    @Test
    public void postOldTransaction() throws Exception {
        Transaction transaction = new Transaction();
        transaction.setAmount(new BigDecimal(12.5));
        transaction.setTimestamp(1478192204000L);
        mvc.perform(MockMvcRequestBuilders.post("/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transaction)))
                .andExpect(status().isNoContent());
    }

    @Test
    public void postCurrentTransaction() throws Exception {
        Transaction transaction = new Transaction();
        transaction.setAmount(new BigDecimal(12.5));
        transaction.setTimestamp(DateTime.now().getMillis());
        mvc.perform(MockMvcRequestBuilders.post("/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transaction)))
                .andExpect(status().isCreated());
    }

    @Test
    public void correctStatistics() throws Exception {
        List<Transaction> transactions = new ArrayList<>();
        Random rand = new Random();
        DateTime now = DateTime.now();
        DateTimeUtils.setCurrentMillisFixed(now.getMillis());

        for (int i = 0; i < 250; ++i) {
            Transaction transaction = new Transaction();
            transaction.setAmount(new BigDecimal(rand.nextDouble()));
            transaction.setTimestamp(now.minusSeconds(rand.nextInt(90)).getMillis());
            transactions.add(transaction);

            mvc.perform(MockMvcRequestBuilders.post("/transactions")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(transaction)));
        }

        Statistic stat = new Statistic();
        transactions.stream()
                .filter(t -> t.timestamp > DateTime.now().getMillis() - 60000)
                .forEach(t -> {
                    stat.incrementCount();
                    stat.incrementSum(t.amount);
                    stat.setMax(t.amount);
                    stat.setMin(t.amount);
                    stat.computeAvg();
                });

        mvc.perform(MockMvcRequestBuilders.get("/statistics")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count", is(stat.getCount())))
                .andExpect(jsonPath("$.sum", is(stat.getSum())))
                .andExpect(jsonPath("$.avg", is(stat.getAvg())))
                .andExpect(jsonPath("$.min", is(stat.getMin())))
                .andExpect(jsonPath("$.max", is(stat.getMax())));
    }
}
