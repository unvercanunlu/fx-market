package tr.unvercanunlu.fxmarket.consumer.impl;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import tr.unvercanunlu.fxmarket.mapper.impl.CsvTextToPriceListMapper;
import tr.unvercanunlu.fxmarket.model.Price;
import tr.unvercanunlu.fxmarket.model.constant.Instrument;
import tr.unvercanunlu.fxmarket.repository.impl.PriceInMemoryRepository;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@SpringBootTest
@EmbeddedKafka
class CsvTextKafkaConsumerTest {

    @SpyBean
    private CsvTextKafkaConsumer csvTextConsumer;

    @MockBean
    private CsvTextToPriceListMapper csvParser;

    @MockBean
    private PriceInMemoryRepository priceInMemoryRepository;

    @Value("${spring.kafka.topic}")
    private String topic;

    @Value(value = "${spring.kafka.bootstrap-servers}")
    private String bootstrapServer;

    @Value(value = "${spring.kafka.group-id}")
    private String groupId;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    private Producer<String, String> producer;

    // private Consumer<String, String> consumer;

    @BeforeEach
    void setUp() {
        // this.prepareConsumer();
        this.prepareProducer();
    }

    @AfterEach
    void tearDown() {
        this.producer.close();
        // this.consumer.close();
    }

    @Test
    void onMessage_shouldConsumeMessage() {
        // data
        String csvText = "106, EUR/USD, 1.1000, 1.2000, 01-06-2020 12:01:01:001";
        int csvTextCount = 1;
        int timeoutMs = 10000;

        Price expected = Price.builder()
                .id(106L)
                .instrument(Instrument.EUR_USD)
                .bidRate(1.1D)
                .askRate(1.2D)
                .build();

        List<Price> priceList = List.of(expected);

        // mocking
        Mockito.when(this.csvParser.map(Mockito.any(String.class))).thenReturn(priceList);

        // producer message
        ProducerRecord<String, String> record = this.generateMessage(csvText);

        // send message
        this.producer.send(record);

        this.producer.flush();

        // capture
        ArgumentCaptor<ConsumerRecord<String, String>> payloadCaptor = ArgumentCaptor.forClass(ConsumerRecord.class);

        Mockito.verify(this.csvTextConsumer, Mockito.timeout(timeoutMs)).onMessage(payloadCaptor.capture());

        ConsumerRecord<String, String> capturedPayload = payloadCaptor.getValue();

        // assertions
        Assertions.assertNotNull(capturedPayload);

        Assertions.assertNotNull(capturedPayload.topic());
        Assertions.assertEquals(capturedPayload.topic(), this.topic);

        Assertions.assertNotNull(capturedPayload.key());

        Assertions.assertNotNull(capturedPayload.value());
        Assertions.assertEquals(capturedPayload.value(), csvText);

        // capture
        ArgumentCaptor<String> csvTextCaptor = ArgumentCaptor.forClass(String.class);

        Mockito.verify(this.csvParser, Mockito.times(csvTextCount)).map(csvTextCaptor.capture());

        String capturedCsvText = csvTextCaptor.getValue();

        // assertions
        Assertions.assertNotNull(capturedCsvText);
        Assertions.assertEquals(capturedCsvText, csvText);

        // capture
        ArgumentCaptor<Price> priceCaptor = ArgumentCaptor.forClass(Price.class);

        Mockito.verify(this.priceInMemoryRepository, Mockito.times(priceList.size())).add(priceCaptor.capture());

        Price capturedPrice = priceCaptor.getValue();

        // assertions
        Assertions.assertNotNull(capturedPrice);
        Assertions.assertEquals(capturedPrice, expected);
    }

    // test helpers
    private ProducerRecord<String, String> generateMessage(String text) {
        return new ProducerRecord<>(this.topic, UUID.randomUUID().toString(), text);
    }

    private Map<String, Object> generateConsumerConfigMap() {
        Map<String, Object> configMap = KafkaTestUtils.consumerProps(this.bootstrapServer, this.groupId, String.valueOf(false));

        configMap.put(ConsumerConfig.GROUP_ID_CONFIG, this.groupId);
        configMap.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, this.bootstrapServer);
        configMap.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configMap.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configMap.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        return configMap;
    }

    private Map<String, Object> generateProducerConfigMap() {
        Map<String, Object> configMap = KafkaTestUtils.producerProps(this.embeddedKafkaBroker);

        configMap.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, this.bootstrapServer);
        configMap.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configMap.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        return configMap;
    }

    /*
    private void prepareConsumer() {
        // consumer config
        Map<String, Object> configMap = this.generateConsumerConfigMap();

        // consumer factory
        DefaultKafkaConsumerFactory<String, String> consumerFactory = new DefaultKafkaConsumerFactory<>(configMap, new StringDeserializer(), new StringDeserializer());

        // consumer
        this.consumer = consumerFactory.createConsumer(this.groupId, "1");

        int partitionCount = 1;

        this.consumer.assign(List.of(new TopicPartition(this.topic, partitionCount)));

        // this.consumer.subscribe(Collections.singleton(this.topic));
    }
    */

    private void prepareProducer() {
        // producer config
        Map<String, Object> configMap = this.generateProducerConfigMap();

        // producer factory
        DefaultKafkaProducerFactory<String, String> producerFactory = new DefaultKafkaProducerFactory<>(configMap, new StringSerializer(), new StringSerializer());

        // producer
        this.producer = producerFactory.createProducer();
    }
}
