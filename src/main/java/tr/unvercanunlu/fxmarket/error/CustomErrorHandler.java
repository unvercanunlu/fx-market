package tr.unvercanunlu.fxmarket.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import tr.unvercanunlu.fxmarket.config.FxMarketConfig;
import tr.unvercanunlu.fxmarket.error.exception.CurrencyNotAvailableException;
import tr.unvercanunlu.fxmarket.error.exception.InstrumentNotAvailableException;
import tr.unvercanunlu.fxmarket.error.exception.PriceNotExistException;

import javax.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class CustomErrorHandler {

    @ExceptionHandler(value = PriceNotExistException.class)
    public ResponseEntity<Map<String, Object>> handlePriceNotExist(PriceNotExistException exception) {
        Map<String, Object> errorMap = new HashMap<>();

        errorMap.put("reason", exception.getMessage());

        Map<String, String> dataMap = new HashMap<>();

        try {
            dataMap.put("instrument", exception.getInstrument().getCode());
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            dataMap.put("timestamp", exception.getTimestamp().format(FxMarketConfig.Date.FORMATTER));
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (dataMap.size() > 0) {
            errorMap.put("data", dataMap);
        }

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .body(errorMap);
    }

    @ExceptionHandler(value = InstrumentNotAvailableException.class)
    public ResponseEntity<Map<String, Object>> handleInstrumentNotAvailable(InstrumentNotAvailableException exception) {
        Map<String, Object> errorMap = new HashMap<>();

        errorMap.put("reason", exception.getMessage());

        Map<String, String> dataMap = new HashMap<>();

        try {
            dataMap.put("currencyFromCode", exception.getFrom().getCode());
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            dataMap.put("currencyToCode", exception.getTo().getCode());
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (dataMap.size() > 0) {
            errorMap.put("data", dataMap);
        }

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(errorMap);
    }

    @ExceptionHandler(value = CurrencyNotAvailableException.class)
    public ResponseEntity<Map<String, Object>> handleCurrencyNotAvailable(CurrencyNotAvailableException exception) {
        Map<String, Object> errorMap = new HashMap<>();

        errorMap.put("reason", exception.getMessage());

        Map<String, String> dataMap = new HashMap<>();

        try {
            dataMap.put("currencyCode", exception.getCurrencyCode());
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (dataMap.size() > 0) {
            errorMap.put("data", dataMap);
        }

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(errorMap);
    }

    @ExceptionHandler(value = ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> handleInternalValidationViolation(ConstraintViolationException exception) {
        Map<String, Object> errorMap = new HashMap<>();

        errorMap.put("reason", "Internal operation is not valid.");

        Map<String, String> fieldViolationMap = new HashMap<>();

        exception.getConstraintViolations()
                .forEach(violation -> fieldViolationMap.put(
                        violation.getPropertyPath().toString(), violation.getMessage()));

        errorMap.put("violations", fieldViolationMap);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(errorMap);
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleRequestValidationViolation(MethodArgumentNotValidException exception) {
        Map<String, Object> errorMap = new HashMap<>();

        errorMap.put("reason", "Request is not valid.");

        Map<String, String> fieldViolationMap = new HashMap<>();

        exception.getBindingResult()
                .getFieldErrors()
                .forEach(fieldError -> fieldViolationMap.put(
                        fieldError.getField(), fieldError.getDefaultMessage()));

        errorMap.put("violations", fieldViolationMap);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(errorMap);
    }
}
