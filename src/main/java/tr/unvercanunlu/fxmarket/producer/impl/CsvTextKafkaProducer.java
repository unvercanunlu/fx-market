package tr.unvercanunlu.fxmarket.producer.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import tr.unvercanunlu.fxmarket.producer.IKafkaProducer;

import java.util.UUID;

@Component
public class CsvTextKafkaProducer implements IKafkaProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    public CsvTextKafkaProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void send(String message, String topic) {
        this.kafkaTemplate.send(topic, UUID.randomUUID().toString(), message);
    }
}
