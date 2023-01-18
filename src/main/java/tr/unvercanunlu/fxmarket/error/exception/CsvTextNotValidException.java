package tr.unvercanunlu.fxmarket.error.exception;

import java.io.Serializable;

public class CsvTextNotValidException extends RuntimeException implements Serializable {

    public CsvTextNotValidException() {
        super("CSV text is not valid.");
    }
}
