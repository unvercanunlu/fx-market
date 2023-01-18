package tr.unvercanunlu.fxmarket.controller.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tr.unvercanunlu.fxmarket.config.FxMarketConfig;
import tr.unvercanunlu.fxmarket.controller.IPriceRestController;
import tr.unvercanunlu.fxmarket.error.exception.CurrencyNotAvailableException;
import tr.unvercanunlu.fxmarket.error.exception.InstrumentNotAvailableException;
import tr.unvercanunlu.fxmarket.model.constant.Currency;
import tr.unvercanunlu.fxmarket.model.constant.Instrument;
import tr.unvercanunlu.fxmarket.model.response.PriceDto;
import tr.unvercanunlu.fxmarket.service.IPriceService;

import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping(path = FxMarketConfig.RestApi.PRICE)
public class PriceRestController implements IPriceRestController {

    private final IPriceService priceService;

    @Autowired
    public PriceRestController(IPriceService priceService) {
        this.priceService = priceService;
    }

    @Override
    @GetMapping(path = "/{currencyFromCode}/{currencyToCode}")
    public ResponseEntity<PriceDto> getLatestPrice(
            @PathVariable(name = "currencyFromCode") String currencyFromCode,
            @PathVariable(name = "currencyToCode") String currencyToCode
    ) {
        Currency from = Optional.ofNullable(Currency.fromCode(currencyFromCode))
                .orElseThrow(() -> new CurrencyNotAvailableException(currencyFromCode));

        Currency to = Optional.ofNullable(Currency.fromCode(currencyToCode))
                .orElseThrow(() -> new CurrencyNotAvailableException(currencyToCode));

        Instrument instrument = Optional.ofNullable(Instrument.fromCurrencies(from, to))
                .orElseThrow(() -> new InstrumentNotAvailableException(from, to));

        LocalDateTime now = LocalDateTime.now();

        PriceDto priceDto = this.priceService.getLatestPrice(instrument, now);

        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(priceDto);
    }
}
