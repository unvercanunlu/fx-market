package tr.unvercanunlu.fxmarket.consumer.impl;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.kafka.test.context.EmbeddedKafka;
import tr.unvercanunlu.fxmarket.mapper.impl.CsvTextToPriceListMapper;
import tr.unvercanunlu.fxmarket.model.Price;
import tr.unvercanunlu.fxmarket.model.constant.Instrument;
import tr.unvercanunlu.fxmarket.producer.impl.PriceKafkaProducer;
import tr.unvercanunlu.fxmarket.repository.impl.PriceInMemoryRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@EmbeddedKafka
@SpringBootTest
class CsvTextKafkaConsumerTest {

    @SpyBean
    private CsvTextKafkaConsumer consumer;

    @MockBean
    private CsvTextToPriceListMapper csvParser;

    @MockBean
    private PriceInMemoryRepository priceInMemoryRepository;

    @Autowired
    private PriceKafkaProducer producer;

    @Value("${topic.name}")
    private String topic;

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

        // mock
        when(this.csvParser.map(any(String.class))).thenReturn(priceList);

        // produce message
        this.producer.send(csvText, this.topic);

        // capture
        ArgumentCaptor<ConsumerRecord<String, String>> payloadCaptor = ArgumentCaptor.forClass(ConsumerRecord.class);

        verify(this.consumer, timeout(timeoutMs)).onMessage(payloadCaptor.capture());

        ConsumerRecord<String, String> capturedPayload = payloadCaptor.getValue();

        // assertions
        assertNotNull(capturedPayload);

        assertNotNull(capturedPayload.topic());
        assertEquals(capturedPayload.topic(), this.topic);

        assertNotNull(capturedPayload.key());

        assertNotNull(capturedPayload.value());
        assertEquals(capturedPayload.value(), csvText);

        // capture
        ArgumentCaptor<String> csvTextCaptor = ArgumentCaptor.forClass(String.class);

        verify(this.csvParser, times(csvTextCount)).map(csvTextCaptor.capture());

        String capturedCsvText = csvTextCaptor.getValue();

        // assertions
        assertNotNull(capturedCsvText);
        assertEquals(capturedCsvText, csvText);

        // capture
        ArgumentCaptor<Price> priceCaptor = ArgumentCaptor.forClass(Price.class);

        verify(this.priceInMemoryRepository, times(priceList.size())).add(priceCaptor.capture());

        Price capturedPrice = priceCaptor.getValue();

        // assertions
        assertNotNull(capturedPrice);
        assertEquals(capturedPrice, expected);
    }
}
