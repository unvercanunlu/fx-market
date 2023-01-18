package tr.unvercanunlu.fxmarket.model.constant;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import java.io.Serializable;
import java.util.Arrays;

@Getter
public enum Currency implements Serializable {

    EUR("EUR", "Euro"),

    USD("USD", "American Dollar"),

    GBP("GBP", "British Pound"),

    JPY("JPY", "Japanese Yen");

    private final String code;

    private final String name;

    Currency(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static Currency fromCode(String code) {
        return Arrays.stream(Currency.values())
                .filter(c -> c.getCode().equalsIgnoreCase(code))
                .findFirst()
                .orElse(null);
    }

    @JsonValue
    public String getCode() {
        return code;
    }
}
