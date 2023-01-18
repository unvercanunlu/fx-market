package tr.unvercanunlu.fxmarket.mapper.impl;

import org.springframework.stereotype.Component;
import tr.unvercanunlu.fxmarket.config.FxMarketConfig;
import tr.unvercanunlu.fxmarket.error.exception.CsvTextNotValidException;
import tr.unvercanunlu.fxmarket.mapper.IMapper;
import tr.unvercanunlu.fxmarket.model.Price;
import tr.unvercanunlu.fxmarket.model.constant.Currency;
import tr.unvercanunlu.fxmarket.model.constant.Instrument;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class CsvTextToPriceListMapper implements IMapper<String, List<Price>> {

    private static final int PART_COUNT = 5;

    private static final int CURRENCY_CODE_COUNT = 2;

    private static final int INVALID_ID_LOWER_BOUND = 0;

    private static final int INVALID_RATE_LOWER_BOUND = 0;

    private static final int ID_LOCATION = 0;

    private static final int INSTRUMENT_LOCATION = 1;

    private static final int CURRENCY_FROM_LOCATION = 0;

    private static final int CURRENCY_TO_LOCATION = 1;

    private static final int BID_RATE_LOCATION = 2;

    private static final int ASK_RATE_LOCATION = 3;

    private static final int TIMESTAMP_LOCATION = 4;

    @Override
    public List<Price> map(String csvText) {
        List<Price> prices = new ArrayList<>();

        String[] lines = csvText.split(FxMarketConfig.CsvParser.LINE_SEPARATOR);

        for (String line : lines) {
            String[] values = line.split(FxMarketConfig.CsvParser.VALUE_SEPARATOR);

            if (values.length != PART_COUNT) {
                throw new CsvTextNotValidException();
            }

            // id
            String idString = values[ID_LOCATION].trim();
            Long id = this.parseStringForId(idString);

            // instrument
            String instrumentString = values[INSTRUMENT_LOCATION].trim();
            Instrument instrument = this.parseStringInstrument(instrumentString);

            // bid rate
            String bidRateString = values[BID_RATE_LOCATION].trim();
            Double bidRate = this.parseStringForRate(bidRateString);

            // ask rate
            String askRateString = values[ASK_RATE_LOCATION].trim();
            Double askRate = this.parseStringForRate(askRateString);

            // timestamp
            String timestampString = values[TIMESTAMP_LOCATION].trim();
            LocalDateTime timestamp = this.parseStringForTimestamp(timestampString);

            // price
            Price price = Price.builder()
                    .id(id)
                    .instrument(instrument)
                    .askRate(askRate)
                    .bidRate(bidRate)
                    .timestamp(timestamp)
                    .build();

            // add to list
            prices.add(price);
        }

        return prices;
    }

    private Instrument parseStringInstrument(String instrumentString) {
        String[] currencyCodes = instrumentString.split(Instrument.INSTRUMENT_SEPARATOR);

        if (currencyCodes.length != CURRENCY_CODE_COUNT) {
            throw new CsvTextNotValidException();
        }

        String currencyFromCode = currencyCodes[CURRENCY_FROM_LOCATION].trim();

        Currency from = Optional.ofNullable(Currency.fromCode(currencyFromCode))
                .orElseThrow(CsvTextNotValidException::new);

        String currencyToCode = currencyCodes[CURRENCY_TO_LOCATION].trim();

        Currency to = Optional.ofNullable(Currency.fromCode(currencyToCode))
                .orElseThrow(CsvTextNotValidException::new);

        return Optional.ofNullable(Instrument.fromCurrencies(from, to))
                .orElseThrow(CsvTextNotValidException::new);
    }

    private Long parseStringForId(String idString) {
        long id;

        try {
            id = Long.parseLong(idString);
        } catch (Exception e) {
            throw new CsvTextNotValidException();
        }

        if (id <= INVALID_ID_LOWER_BOUND) {
            throw new CsvTextNotValidException();
        }

        return id;
    }

    private Double parseStringForRate(String rateString) {
        double rate;

        try {
            rate = Double.parseDouble(rateString);
        } catch (Exception e) {
            throw new CsvTextNotValidException();
        }

        if (rate <= INVALID_RATE_LOWER_BOUND) {
            throw new CsvTextNotValidException();
        }

        return rate;
    }

    private LocalDateTime parseStringForTimestamp(String timestampString) {
        LocalDateTime timestamp;

        try {
            timestamp = LocalDateTime.parse(timestampString, FxMarketConfig.Date.FORMATTER);
        } catch (Exception e) {
            throw new CsvTextNotValidException();
        }

        return timestamp;
    }
}
