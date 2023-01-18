package tr.unvercanunlu.fxmarket.error.exception;

import lombok.Getter;
import tr.unvercanunlu.fxmarket.model.constant.Instrument;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
public class PriceNotExistException extends RuntimeException implements Serializable {

    private final Instrument instrument;

    private final LocalDateTime timestamp;

    public PriceNotExistException(Instrument instrument, LocalDateTime timestamp) {
        super("Price from Currency with code " + instrument.getFrom().getCode() + " to Currency with code " + instrument.getTo().getCode() + " does not exist.");
        this.instrument = instrument;
        this.timestamp = timestamp;
    }
}
