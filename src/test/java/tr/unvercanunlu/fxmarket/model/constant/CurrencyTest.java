package tr.unvercanunlu.fxmarket.model.constant;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CurrencyTest {

    @Test
    void fromCode_existCurrencyCode_shouldReturnCurrency() {
        Assertions.assertEquals(Currency.EUR, Currency.fromCode("EUR"));
        Assertions.assertEquals(Currency.USD, Currency.fromCode("USD"));
        Assertions.assertEquals(Currency.GBP, Currency.fromCode("GBP"));
        Assertions.assertEquals(Currency.JPY, Currency.fromCode("JPY"));
    }

    @Test
    void fromCode_nullOrBlankOrNotExistCurrencyCode_shouldReturnNull() {
        Assertions.assertNull(Currency.fromCode(null));
        Assertions.assertNull(Currency.fromCode("TRY")); // Turkish Lira
        Assertions.assertNull(Currency.fromCode(""));
    }
}
