package tr.unvercanunlu.fxmarket.consumer.impl;

import lombok.Getter;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tr.unvercanunlu.fxmarket.consumer.IKafkaConsumer;
import tr.unvercanunlu.fxmarket.mapper.IMapper;
import tr.unvercanunlu.fxmarket.model.Price;
import tr.unvercanunlu.fxmarket.repository.IPriceRepository;

import java.util.ArrayList;
import java.util.List;

@Getter
@Component
public class PriceKafkaConsumer implements IKafkaConsumer<String, String> {

    private final IPriceRepository priceRepository;

    private final IMapper<String, List<Price>> csvParser;

    @Autowired
    public PriceKafkaConsumer(
            IPriceRepository priceRepository,
            IMapper<String, List<Price>> csvParser
    ) {
        this.priceRepository = priceRepository;
        this.csvParser = csvParser;
    }

    @Override
    @KafkaListener(topics = "${topic.name}", containerFactory = "listenerFactory")
    public void onMessage(ConsumerRecord<String, String> payload) {
        String csvText = payload.value();

        List<Price> prices = new ArrayList<>();

        try {
            prices = this.csvParser.map(csvText);
        } catch (Exception e) {
            e.printStackTrace();
        }

        prices.forEach(this.priceRepository::add);
    }
}
