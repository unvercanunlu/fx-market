package tr.unvercanunlu.fxmarket.error.exception;

import java.io.Serializable;

public class CsvNotValidException extends RuntimeException implements Serializable {

    public CsvNotValidException() {
        super("CSV is not valid.");
    }
}
