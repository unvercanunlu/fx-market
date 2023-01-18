package tr.unvercanunlu.fxmarket.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;

@Validated
public interface IKafkaConsumer<K, V> {

    void onMessage(@NotNull(message = "Consumer payload should not be null.") ConsumerRecord<K, V> payload);
}
