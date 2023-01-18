package tr.unvercanunlu.fxmarket.model.constant;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import java.io.Serializable;
import java.util.Arrays;

@Getter
public enum Instrument implements Serializable {

    EUR_USD(Currency.EUR, Currency.USD),
    GBP_USD(Currency.GBP, Currency.USD),
    EUR_JPY(Currency.EUR, Currency.JPY);

    private final Currency from;

    private final Currency to;

    Instrument(Currency from, Currency to) {
        this.from = from;
        this.to = to;
    }

    public static Instrument fromCurrencies(Currency from, Currency to) {
        return Arrays.stream(Instrument.values())
                .filter(i -> i.getFrom().equals(from) && i.getTo().equals(to))
                .findFirst()
                .orElse(null);
    }

    @JsonValue
    public String getCode() {
        return this.from.getCode() + "/" + this.to.getCode();
    }
}
