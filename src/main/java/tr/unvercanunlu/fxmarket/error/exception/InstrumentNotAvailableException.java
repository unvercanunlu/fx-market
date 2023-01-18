package tr.unvercanunlu.fxmarket.error.exception;

import lombok.Getter;
import tr.unvercanunlu.fxmarket.model.constant.Currency;

import java.io.Serializable;

@Getter
public class InstrumentNotAvailableException extends RuntimeException implements Serializable {

    private final Currency from;

    private final Currency to;

    public InstrumentNotAvailableException(Currency from, Currency to) {
        super("Instrument from Currency with code " + from.getCode() + " to Currency with code " + to.getCode() + " is not available.");
        this.from = from;
        this.to = to;
    }
}
