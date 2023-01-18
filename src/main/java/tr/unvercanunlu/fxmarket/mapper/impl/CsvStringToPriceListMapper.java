package tr.unvercanunlu.fxmarket.mapper.impl;

import org.springframework.stereotype.Component;
import tr.unvercanunlu.fxmarket.config.DateTimeConfig;
import tr.unvercanunlu.fxmarket.error.exception.CurrencyNotAvailableException;
import tr.unvercanunlu.fxmarket.error.exception.InstrumentNotAvailableException;
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
public class CsvStringToPriceListMapper implements IMapper<String, List<Price>> {

    private static final String VALUE_SEPARATOR = ",";

    private static final String LINE_SEPARATOR = "\n";

    private static final String INSTRUMENT_SEPARATOR = "/";

    @Override
    public List<Price> map(String csvString) {
        List<Price> prices = new ArrayList<>();

        String[] lines = csvString.split(LINE_SEPARATOR);

        for (String line : lines) {
            String[] values = line.split(VALUE_SEPARATOR);

            if (values.length != 5) {
                continue;
            }

            String idString = values[0].trim();
            Long id = Long.parseLong(idString);

            String instrumentString = values[1].trim();
            String[] currencyCodes = instrumentString.split(INSTRUMENT_SEPARATOR);

            if (currencyCodes.length != 2) {
                continue;
            }

            String currencyFromCode = currencyCodes[0].trim();

            Currency from = Optional.ofNullable(Currency.fromCode(currencyFromCode))
                    .orElseThrow(() -> new CurrencyNotAvailableException(currencyFromCode));

            String currencyToCode = currencyCodes[1].trim();

            Currency to = Optional.ofNullable(Currency.fromCode(currencyToCode))
                    .orElseThrow(() -> new CurrencyNotAvailableException(currencyToCode));

            Instrument instrument = Optional.ofNullable(Instrument.fromCurrencies(from, to))
                    .orElseThrow(() -> new InstrumentNotAvailableException(from, to));

            String bidString = values[2].trim();
            Double bid = Double.valueOf(bidString);

            String askString = values[3].trim();
            Double ask = Double.valueOf(askString);

            String timestampString = values[4].trim();
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DateTimeConfig.DATE_TIME_FORMAT);
            LocalDateTime timestamp = LocalDateTime.parse(timestampString, dateTimeFormatter);

            Price price = Price.builder()
                    .id(id)
                    .instrument(instrument)
                    .ask(ask)
                    .bid(bid)
                    .timestamp(timestamp)
                    .build();

            prices.add(price);
        }

        return prices;
    }
}
