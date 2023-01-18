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

import java.util.List;

@Getter
@Component
public class PriceKafkaConsumer implements IKafkaConsumer<String, String> {

    private final IPriceRepository priceRepository;

    private final IMapper<String, List<Price>> csvStringToPriceListMapper;

    @Autowired
    public PriceKafkaConsumer(
            IPriceRepository priceRepository,
            IMapper<String, List<Price>> csvStringToPriceListMapper
    ) {
        this.priceRepository = priceRepository;
        this.csvStringToPriceListMapper = csvStringToPriceListMapper;
    }

    @Override
    @KafkaListener(topics = "${topic.name}", containerFactory = "listenerFactory")
    public void onMessage(ConsumerRecord<String, String> payload) {
        String csvString = payload.value();
        List<Price> prices = this.csvStringToPriceListMapper.map(csvString);
        prices.forEach(this.priceRepository::add);
    }
}
