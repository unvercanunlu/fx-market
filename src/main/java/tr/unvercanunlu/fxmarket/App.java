package tr.unvercanunlu.fxmarket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class App {

    /*
    @Value("${spring.kafka.topic}")
    private String topic;

    private final CsvTextKafkaProducer producer;

    @Autowired
    public App(CsvTextKafkaProducer producer) {
        this.producer = producer;
    }
    */

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    /*
    @EventListener(value = ApplicationReadyEvent.class)
    public void initializeDatabase() {
        List<String> csvTexts = prepareCsvTexts();
        csvTexts.forEach(csvText -> this.producer.send(csvText, this.topic));
    }

    private List<String> prepareCsvTexts() {
        String line1 = "106, EUR/USD, 1.1000, 1.2000, 01-06-2020 12:01:01:001";
        String line2 = "107, EUR/JPY, 119.60,119.90,01-06-2020 12:01:02:002";
        String line3 = "108, GBP/USD, 1.2500,1.2560,01-06-2020 12:01:02:002";
        String line4 = "109, GBP/USD, 1.2499,1.2561,01-06-2020 12:01:02:100";
        String line5 = "110, EUR/JPY, 119.61,119.91,01-06-2020 12:01:02:110";
        String csvText1 = line1 + "\n" + line2;
        String csvText2 = line3 + "\n" + line4 + "\n" + line5;
        return List.of(csvText1, csvText2);
    }
    */
}
