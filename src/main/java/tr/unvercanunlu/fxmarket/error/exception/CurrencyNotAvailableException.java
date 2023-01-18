package tr.unvercanunlu.fxmarket.error.exception;

import java.io.Serializable;

public class CurrencyNotAvailableException extends RuntimeException implements Serializable {

    private final String currencyCode;

    public CurrencyNotAvailableException(String currencyCode) {
        super("Currency with code " + currencyCode + " is not available.");
        this.currencyCode = currencyCode;
    }
}
