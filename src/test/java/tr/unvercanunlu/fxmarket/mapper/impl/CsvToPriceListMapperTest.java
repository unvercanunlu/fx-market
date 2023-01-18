package tr.unvercanunlu.fxmarket.mapper.impl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import tr.unvercanunlu.fxmarket.config.DateTimeConfig;
import tr.unvercanunlu.fxmarket.error.exception.CsvNotValidException;
import tr.unvercanunlu.fxmarket.mapper.IMapper;
import tr.unvercanunlu.fxmarket.model.Price;
import tr.unvercanunlu.fxmarket.model.constant.Instrument;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CsvToPriceListMapperTest {

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DateTimeConfig.DATE_TIME_FORMAT);

    @Autowired
    private IMapper<String, List<Price>> CsvToPriceListMapper;

    @Test
    void map_validSingleLine_shouldReturnPriceList() {
        // data
        Price price = Price.builder()
                .id(106L)
                .bid(1.1D)
                .ask(1.2D)
                .instrument(Instrument.EUR_USD)
                .timestamp(LocalDateTime.parse("01-06-2020 12:01:01:001", this.dateTimeFormatter))
                .build();

        // parameter
        String csv = "106, EUR/USD, 1.1000, 1.2000, 01-06-2020 12:01:01:001";

        // expected
        List<Price> expected = Collections.singletonList(price);

        // result
        List<Price> result = new ArrayList<>();

        // method call
        try {
            result = this.CsvToPriceListMapper.map(csv);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // asserts
        assertNotNull(result);

        assertEquals(1, result.size());
        assertEquals(expected.size(), result.size());

        assertNotNull(result.get(0));

        assertNotNull(result.get(0).getId());
        assertEquals(expected.get(0).getId(), result.get(0).getId());

        assertNotNull(result.get(0).getInstrument());
        assertEquals(expected.get(0).getInstrument(), result.get(0).getInstrument());

        assertNotNull(result.get(0).getAsk());
        assertEquals(expected.get(0).getAsk(), result.get(0).getAsk());

        assertNotNull(result.get(0).getBid());
        assertEquals(expected.get(0).getBid(), result.get(0).getBid());

        assertNotNull(result.get(0).getTimestamp());
        assertEquals(expected.get(0).getTimestamp(), result.get(0).getTimestamp());
    }

    @Test
    void map_validMultiLine_shouldReturnPriceList() {
        // data
        Price price1 = Price.builder()
                .id(106L)
                .bid(1.1D)
                .ask(1.2D)
                .instrument(Instrument.EUR_USD)
                .timestamp(LocalDateTime.parse("01-06-2020 12:01:01:001", this.dateTimeFormatter))
                .build();

        Price price2 = Price.builder()
                .id(107L)
                .bid(119.6D)
                .ask(119.9D)
                .instrument(Instrument.EUR_JPY)
                .timestamp(LocalDateTime.parse("01-06-2020 12:01:02:002", this.dateTimeFormatter))
                .build();

        // parameter
        String csv = "106, EUR/USD, 1.1000, 1.2000, 01-06-2020 12:01:01:001\n" +
                "107, EUR/JPY, 119.60,119.90,01-06-2020 12:01:02:002";

        // expected
        List<Price> expected = List.of(price1, price2);

        // result
        List<Price> result = new ArrayList<>();

        // method call
        try {
            result = this.CsvToPriceListMapper.map(csv);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // asserts
        assertNotNull(result);

        assertEquals(2, result.size());
        assertEquals(expected.size(), result.size());

        assertNotNull(result.get(0));

        assertNotNull(result.get(0).getId());
        assertEquals(expected.get(0).getId(), result.get(0).getId());

        assertNotNull(result.get(0).getInstrument());
        assertEquals(expected.get(0).getInstrument(), result.get(0).getInstrument());

        assertNotNull(result.get(0).getAsk());
        assertEquals(expected.get(0).getAsk(), result.get(0).getAsk());

        assertNotNull(result.get(0).getBid());
        assertEquals(expected.get(0).getBid(), result.get(0).getBid());

        assertNotNull(result.get(0).getTimestamp());
        assertEquals(expected.get(0).getTimestamp(), result.get(0).getTimestamp());

        assertNotNull(result.get(1));

        assertNotNull(result.get(1).getId());
        assertEquals(expected.get(1).getId(), result.get(1).getId());

        assertNotNull(result.get(1).getInstrument());
        assertEquals(expected.get(1).getInstrument(), result.get(1).getInstrument());

        assertNotNull(result.get(1).getAsk());
        assertEquals(expected.get(1).getAsk(), result.get(1).getAsk());

        assertNotNull(result.get(1).getBid());
        assertEquals(expected.get(1).getBid(), result.get(1).getBid());

        assertNotNull(result.get(1).getTimestamp());
        assertEquals(expected.get(1).getTimestamp(), result.get(1).getTimestamp());
    }

    @Test
    void map_missingOrInvalidParts_shouldThrowCsvNotValidException() {
        // data
        String invalidCurrencyCode = "";
        String notExistCurrencyCode = "TRY"; // Turkish Lira
        String notExistInstrument = "JPY/EUR";
        String invalidInstrument = "abc";
        long invalidId = -1L;
        double invalidRate = -1D;
        String invalidTimestamp = "abc";

        // parameter
        String csvMissingId = "EUR/USD, 1.1000, 1.2000, 01-06-2020 12:01:01:001";
        String csvMissingInstrument = "106, 1.1000, 1.2000, 01-06-2020 12:01:01:001";
        String csvMissingBid = "106, EUR/USD, 1.2000, 01-06-2020 12:01:01:001";
        String csvMissingAsk = "106, EUR/USD, 1.1000, 01-06-2020 12:01:01:001";
        String csvMissingRates = "106, EUR/USD, 01-06-2020 12:01:01:001";
        String csvMissingTimestamp = "106, EUR/USD, 1.1000, 1.200";
        String csvInvalidId = invalidId + ", EUR/JPY, 1.1000, 1.2000, 01-06-2020 12:01:01:001";
        String csvInvalidCurrencyCode = "106, " + invalidCurrencyCode + "/USD, 1.1000, 1.2000, 01-06-2020 12:01:01:001";
        String csvNotExistCurrencyCode = "106, USD/" + notExistCurrencyCode + ", 1.1000, 1.2000, 01-06-2020 12:01:01:001";
        String csvNotExistInstrument = "106," + notExistInstrument + ", 1.1000, 1.2000, 01-06-2020 12:01:01:001";
        String csvInvalidInstrument = "106," + invalidInstrument + ", 1.1000, 1.2000, 01-06-2020 12:01:01:001";
        String csvInvalidBid = "106, EUR/JPY, " + invalidRate + ", 1.2000, 01-06-2020 12:01:01:001";
        String csvInvalidAsk = "106, EUR/JPY, 1.100, " + invalidRate + ", 01-06-2020 12:01:01:001";
        String csvInvalidTimestamp = "106, EUR/JPY, 1.100, 1.200, " + invalidTimestamp;

        List<String> parameters = List.of(
                csvMissingId,
                csvMissingInstrument,
                csvMissingAsk,
                csvMissingBid,
                csvMissingRates,
                csvMissingTimestamp,
                csvInvalidId,
                csvInvalidCurrencyCode,
                csvNotExistCurrencyCode,
                csvNotExistInstrument,
                csvInvalidInstrument,
                csvInvalidAsk,
                csvInvalidBid,
                csvInvalidTimestamp
        );

        for (String csv : parameters) {
            // method call and assert
            try {
                assertThrows(CsvNotValidException.class, () -> this.CsvToPriceListMapper.map(csv));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
