package tr.unvercanunlu.fxmarket.mapper.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import tr.unvercanunlu.fxmarket.config.FxMarketConfig;
import tr.unvercanunlu.fxmarket.error.exception.CsvTextNotValidException;
import tr.unvercanunlu.fxmarket.mapper.IMapper;
import tr.unvercanunlu.fxmarket.model.Price;
import tr.unvercanunlu.fxmarket.model.constant.Currency;
import tr.unvercanunlu.fxmarket.model.constant.Instrument;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class CsvTextToPriceListMapperTest {

    @Autowired
    private IMapper<String, List<Price>> csvParser;

    @Test
    void map_validSingleLineCsvText_shouldReturnPriceList() {
        // data
        String timestampTextValid = "01-06-2020 12:01:01:001";
        LocalDateTime timestampValid = LocalDateTime.parse(timestampTextValid, FxMarketConfig.Date.FORMATTER);
        double rateValid = 1D;
        long idValid = 1;
        Instrument instrumentValid = Instrument.EUR_USD;

        // price
        Price price = Price.builder()
                .id(idValid)
                .bidRate(rateValid)
                .askRate(rateValid)
                .instrument(instrumentValid)
                .timestamp(timestampValid)
                .build();

        // line
        String line = this.lineBuilder(price.getId(), price.getInstrument().getFrom().getCode(),
                price.getInstrument().getTo().getCode(), price.getBidRate(), price.getAskRate(), price.getTimestamp());

        // csv text
        String csvText = this.csvTextBuilder(line);

        // expected
        List<Price> expected = List.of(price);

        // result
        List<Price> actual = new ArrayList<>();

        // method call
        try {
            actual = this.csvParser.map(csvText);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // assertions
        Assertions.assertNotNull(actual);

        Assertions.assertEquals(expected.size(), actual.size());

        for (int i = 0; i < actual.size(); i++) {
            Price actualPrice = actual.get(i);
            Assertions.assertNotNull(actualPrice);

            Price expectedPrice = expected.get(i);

            Assertions.assertNotNull(actualPrice.getId());
            Assertions.assertEquals(expectedPrice.getId(), actualPrice.getId());

            Assertions.assertNotNull(actualPrice.getInstrument());
            Assertions.assertEquals(expectedPrice.getInstrument(), actualPrice.getInstrument());

            Assertions.assertNotNull(actualPrice.getAskRate());
            Assertions.assertEquals(expectedPrice.getAskRate(), actualPrice.getAskRate());

            Assertions.assertNotNull(actualPrice.getBidRate());
            Assertions.assertEquals(expectedPrice.getBidRate(), actualPrice.getBidRate());

            Assertions.assertNotNull(actualPrice.getTimestamp());
            Assertions.assertEquals(expectedPrice.getTimestamp(), actualPrice.getTimestamp());
        }
    }

    @Test
    void map_validMultiLineCsvText_shouldReturnPriceList() {
        // data
        String timestampTextValid = "01-06-2020 12:01:01:001";
        LocalDateTime timestampValid = LocalDateTime.parse(timestampTextValid, FxMarketConfig.Date.FORMATTER);
        double rateValid = 1D;
        long idValid = 1L;
        Instrument instrumentValid = Instrument.EUR_USD;

        // prices
        Price price1 = Price.builder()
                .id(idValid)
                .bidRate(rateValid)
                .askRate(rateValid)
                .instrument(instrumentValid)
                .timestamp(timestampValid)
                .build();

        Price price2 = Price.builder()
                .id(idValid)
                .bidRate(rateValid)
                .askRate(rateValid)
                .instrument(instrumentValid)
                .timestamp(timestampValid)
                .build();

        // lines
        String line1 = this.lineBuilder(price1.getId(), price1.getInstrument().getFrom().getCode(),
                price1.getInstrument().getTo().getCode(), price1.getBidRate(), price1.getAskRate(), price1.getTimestamp());

        String line2 = this.lineBuilder(price2.getId(), price2.getInstrument().getFrom().getCode(),
                price2.getInstrument().getTo().getCode(), price2.getBidRate(), price2.getAskRate(), price2.getTimestamp());

        // csv text
        String csvText = this.csvTextBuilder(line1, line2);

        // expected
        List<Price> expected = List.of(price1, price2);

        // result
        List<Price> actual = new ArrayList<>();

        // method call
        try {
            actual = this.csvParser.map(csvText);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // assertions
        Assertions.assertNotNull(actual);

        Assertions.assertEquals(expected.size(), actual.size());

        for (int i = 0; i < actual.size(); i++) {
            Price actualPrice = actual.get(i);
            Assertions.assertNotNull(actualPrice);

            Price expectedPrice = expected.get(i);

            Assertions.assertNotNull(actualPrice.getId());
            Assertions.assertEquals(expectedPrice.getId(), actualPrice.getId());

            Assertions.assertNotNull(actualPrice.getInstrument());
            Assertions.assertEquals(expectedPrice.getInstrument(), actualPrice.getInstrument());

            Assertions.assertNotNull(actualPrice.getAskRate());
            Assertions.assertEquals(expectedPrice.getAskRate(), actualPrice.getAskRate());

            Assertions.assertNotNull(actualPrice.getBidRate());
            Assertions.assertEquals(expectedPrice.getBidRate(), actualPrice.getBidRate());

            Assertions.assertNotNull(actualPrice.getTimestamp());
            Assertions.assertEquals(expectedPrice.getTimestamp(), actualPrice.getTimestamp());
        }
    }

    @Test
    void map_missingOrInvalidOrNotExistPartsCsvText_shouldThrowCsvTextNotValidException() {
        // data
        long idValid = 1L;
        long idInvalid = -1L;
        double rateValid = 1D;
        double rateInvalid = -1D;
        String currencyCodeValid = Currency.USD.getCode();
        String currencyCodeInvalid = "abc";
        String currencyCodeNotExist = "TRY"; // Turkish Lira
        String timestampTextValid = "01-06-2020 12:01:01:001";
        LocalDateTime timestampValid = LocalDateTime.parse(timestampTextValid, FxMarketConfig.Date.FORMATTER);
        String timestampTextInvalid = "abc";

        // lines
        String csvTextMissingId = this.csvTextBuilder(
                this.lineBuilder(null, currencyCodeValid, currencyCodeValid, rateValid, rateValid, timestampValid));
        String csvTextMissingInstrument = this.csvTextBuilder(
                this.lineBuilder(idValid, null, null, rateValid, rateValid, timestampValid));
        String csvTextMissingBidRate = this.csvTextBuilder(
                this.lineBuilder(idValid, currencyCodeValid, currencyCodeValid, null, rateValid, timestampValid));
        String csvTextMissingAskRate = this.csvTextBuilder(this.lineBuilder(
                idValid, currencyCodeValid, currencyCodeValid, rateValid, null, timestampValid));
        String csvTextMissingTimestamp = this.csvTextBuilder(
                this.lineBuilder(idValid, currencyCodeValid, currencyCodeValid, rateValid, rateValid, null));
        String csvTextInvalidId = this.csvTextBuilder(
                this.lineBuilder(idInvalid, currencyCodeValid, currencyCodeValid, rateValid, rateValid, timestampValid));
        String csvTextInvalidCurrencyCode1 = this.csvTextBuilder(
                this.lineBuilder(idValid, currencyCodeInvalid, currencyCodeValid, rateValid, rateValid, timestampValid));
        String csvTextInvalidCurrencyCode2 = this.csvTextBuilder(
                this.lineBuilder(idValid, currencyCodeValid, currencyCodeInvalid, rateValid, rateValid, timestampValid));
        String csvTextInvalidBidRate = this.csvTextBuilder(
                this.lineBuilder(idValid, currencyCodeValid, currencyCodeValid, rateInvalid, rateValid, timestampValid));
        String csvTextInvalidAskRate = this.csvTextBuilder(
                this.lineBuilder(idValid, currencyCodeValid, currencyCodeValid, rateValid, rateInvalid, timestampValid));
        String csvTextInvalidTimestamp = this.csvTextBuilder(
                this.lineBuilder(idValid, currencyCodeValid, currencyCodeValid, rateValid, rateValid, timestampTextInvalid));
        String csvTextNotExistCurrencyCode1 = this.csvTextBuilder(
                this.lineBuilder(idValid, currencyCodeNotExist, currencyCodeValid, rateValid, rateValid, timestampValid));
        String csvTextNotExistCurrencyCode2 = this.csvTextBuilder(
                this.lineBuilder(idValid, currencyCodeValid, currencyCodeNotExist, rateValid, rateValid, timestampValid));
        String csvTextNotExistInstrument = this.csvTextBuilder(
                this.lineBuilder(idValid, currencyCodeNotExist, currencyCodeNotExist, rateValid, rateValid, timestampValid));

        // csv text list
        List<String> csvTextlist = List.of(
                // missing
                csvTextMissingId, csvTextMissingInstrument, csvTextMissingBidRate, csvTextMissingAskRate, csvTextMissingTimestamp,
                // invalid
                csvTextInvalidId, csvTextInvalidCurrencyCode1, csvTextInvalidCurrencyCode2, csvTextInvalidAskRate, csvTextInvalidBidRate, csvTextInvalidTimestamp,
                // not exist
                csvTextNotExistCurrencyCode1, csvTextNotExistCurrencyCode2, csvTextNotExistInstrument
        );

        for (String csvTextInvalid : csvTextlist) {
            // method call and assert
            try {
                Assertions.assertThrows(CsvTextNotValidException.class, () -> this.csvParser.map(csvTextInvalid));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // test case helpers
    private String csvTextBuilder(String... lines) {
        StringBuilder builder = new StringBuilder();


        for (String line : lines) {
            builder.append(line).append("\n");
        }

        return builder.toString();
    }

    private String lineBuilder(
            Long id, String currencyFromCode, String currencyToCode,
            Double bidRate, Double askRate, Object timestamp
    ) {
        StringBuilder builder = new StringBuilder();

        if (id != null) {
            builder.append(id);
        }

        builder.append(FxMarketConfig.CsvParser.VALUE_SEPARATOR);

        if (currencyFromCode != null) {
            builder.append(currencyFromCode);

            if (currencyToCode != null) {
                builder.append(Instrument.INSTRUMENT_SEPARATOR);
                builder.append(currencyToCode);
            }
        } else {
            builder.append(Instrument.INSTRUMENT_SEPARATOR);

            if (currencyToCode != null) {
                builder.append(currencyToCode);
            }
        }

        builder.append(FxMarketConfig.CsvParser.VALUE_SEPARATOR);

        if (bidRate != null) {
            builder.append(bidRate);
        }

        builder.append(FxMarketConfig.CsvParser.VALUE_SEPARATOR);

        if (askRate != null) {
            builder.append(askRate);
        }

        builder.append(FxMarketConfig.CsvParser.VALUE_SEPARATOR);

        if (timestamp != null) {
            if (timestamp instanceof LocalDateTime) {
                builder.append(((LocalDateTime) timestamp).format(FxMarketConfig.Date.FORMATTER));
            } else {
                builder.append(timestamp);
            }
        }

        return builder.toString();
    }
}
