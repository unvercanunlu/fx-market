package tr.unvercanunlu.fxmarket.model.constant;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class InstrumentTest {

    @Test
    void fromCurrencies_existCurrency_shouldReturnInstrument() {
        Assertions.assertEquals(Instrument.EUR_USD, Instrument.fromCurrencies(Currency.EUR, Currency.USD));
        Assertions.assertEquals(Instrument.EUR_JPY, Instrument.fromCurrencies(Currency.EUR, Currency.JPY));
        Assertions.assertEquals(Instrument.GBP_USD, Instrument.fromCurrencies(Currency.GBP, Currency.USD));
    }

    @Test
    void fromCurrencies_nullOrNotExistCurrency_shouldReturnNull() {
        Assertions.assertNull(Instrument.fromCurrencies(null, Currency.USD));
        Assertions.assertNull(Instrument.fromCurrencies(Currency.EUR, null));
        Assertions.assertNull(Instrument.fromCurrencies(Currency.GBP, Currency.JPY));
    }
}
