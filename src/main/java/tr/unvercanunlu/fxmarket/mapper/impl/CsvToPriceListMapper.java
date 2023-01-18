package tr.unvercanunlu.fxmarket.mapper.impl;

import org.springframework.stereotype.Component;
import tr.unvercanunlu.fxmarket.config.DateTimeConfig;
import tr.unvercanunlu.fxmarket.error.exception.CsvNotValidException;
import tr.unvercanunlu.fxmarket.mapper.IMapper;
import tr.unvercanunlu.fxmarket.model.Price;
import tr.unvercanunlu.fxmarket.model.constant.Currency;
import tr.unvercanunlu.fxmarket.model.constant.Instrument;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class CsvToPriceListMapper implements IMapper<String, List<Price>> {

    private static final String VALUE_SEPARATOR = ",";
    private static final String LINE_SEPARATOR = "\n";
    private static final String INSTRUMENT_SEPARATOR = "/";
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DateTimeConfig.DATE_TIME_FORMAT);

    @Override
    public List<Price> map(String csv) {
        List<Price> prices = new ArrayList<>();

        String[] lines = csv.split(LINE_SEPARATOR);

        for (String line : lines) {
            String[] values = line.split(VALUE_SEPARATOR);

            if (values.length != 5) {
                throw new CsvNotValidException();
            }

            // id
            String idString = values[0].trim();
            Long id = this.parseStringForId(idString);

            // instrument
            String instrumentString = values[1].trim();
            Instrument instrument = this.parseStringInstrument(instrumentString);

            // bid
            String bidString = values[2].trim();
            Double bid = this.parseStringForRate(bidString);

            // ask
            String askString = values[3].trim();
            Double ask = this.parseStringForRate(askString);

            // timestamp
            String timestampString = values[4].trim();
            LocalDateTime timestamp = this.parseStringForTimestamp(timestampString);

            // price
            Price price = Price.builder()
                    .id(id)
                    .instrument(instrument)
                    .ask(ask)
                    .bid(bid)
                    .timestamp(timestamp)
                    .build();

            // add to list
            prices.add(price);
        }

        return prices;
    }

    private Instrument parseStringInstrument(String instrumentString) {
        String[] currencyCodes = instrumentString.split(INSTRUMENT_SEPARATOR);

        if (currencyCodes.length != 2) {
            throw new CsvNotValidException();
        }

        String currencyFromCode = currencyCodes[0].trim();

        Currency from = Optional.ofNullable(Currency.fromCode(currencyFromCode))
                .orElseThrow(CsvNotValidException::new);

        String currencyToCode = currencyCodes[1].trim();

        Currency to = Optional.ofNullable(Currency.fromCode(currencyToCode))
                .orElseThrow(CsvNotValidException::new);

        return Optional.ofNullable(Instrument.fromCurrencies(from, to))
                .orElseThrow(CsvNotValidException::new);
    }

    private Long parseStringForId(String idString) {
        long id;

        try {
            id = Long.parseLong(idString);
        } catch (Exception e) {
            throw new CsvNotValidException();
        }

        if (id <= 0) {
            throw new CsvNotValidException();
        }

        return id;
    }

    private Double parseStringForRate(String rateString) {
        double rate;

        try {
            rate = Double.parseDouble(rateString);
        } catch (Exception e) {
            throw new CsvNotValidException();
        }

        if (rate <= 0) {
            throw new CsvNotValidException();
        }

        return rate;
    }

    private LocalDateTime parseStringForTimestamp(String timestampString) {
        LocalDateTime timestamp;

        try {
            timestamp = LocalDateTime.parse(timestampString, this.dateTimeFormatter);
        } catch (Exception e) {
            throw new CsvNotValidException();
        }

        return timestamp;
    }
}
