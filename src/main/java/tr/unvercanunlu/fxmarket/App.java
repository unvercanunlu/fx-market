package tr.unvercanunlu.fxmarket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tr.unvercanunlu.fxmarket.producer.impl.PriceKafkaProducer;

@SpringBootApplication
public class App {

    @Autowired
    private PriceKafkaProducer producer;

    @Value(value = "${topic.name}")
    private String topic;

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    /*
    @EventListener(ApplicationReadyEvent.class)
    public void doSomethingAfterStartup() {
        List<String> messages = prepareMessages();
        messages.forEach(message -> this.producer.send(message, this.topic));
    }

    private List<String> prepareMessages() {
        String priceLine1 = "106, EUR/USD, 1.1000, 1.2000, 01-06-2020 12:01:01:001";
        String priceLine2 = "107, EUR/JPY, 119.60,119.90,01-06-2020 12:01:02:002";
        String priceLine3 = "108, GBP/USD, 1.2500,1.2560,01-06-2020 12:01:02:002";
        String priceLine4 = "109, GBP/USD, 1.2499,1.2561,01-06-2020 12:01:02:100";
        String priceLine5 = "110, EUR/JPY, 119.61,119.91,01-06-2020 12:01:02:110";

        String priceMessage1 = priceLine1 + "\n" + priceLine2;
        String priceMessage2 = priceLine3 + "\n" + priceLine4 + "\n" + priceLine5;

        return List.of(priceMessage1, priceMessage2);
    }
     */
}
