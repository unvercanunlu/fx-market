package tr.unvercanunlu.fxmarket.producer;

public interface IKafkaProducer {

    void send(String message, String topic);
}
