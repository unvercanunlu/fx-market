package tr.unvercanunlu.fxmarket.service.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import tr.unvercanunlu.fxmarket.error.exception.PriceNotExistException;
import tr.unvercanunlu.fxmarket.model.Price;
import tr.unvercanunlu.fxmarket.model.constant.Instrument;
import tr.unvercanunlu.fxmarket.model.response.PriceDto;
import tr.unvercanunlu.fxmarket.repository.IPriceRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
class PriceServiceTest {

    @Autowired
    private PriceService priceService;

    @MockBean
    private IPriceRepository priceRepository;

    @Value(value = "${commission-rate}")
    private Double commissionRate;


    @Test
    void getLatestPrice_validInstrumentAndTimestamp_containPrice_shouldReturnPrice() {
        // data
        Price price = Price.builder()
                .id(1L)
                .instrument(Instrument.EUR_USD)
                .askRate(1.1D)
                .bidRate(1.1D)
                .timestamp(LocalDateTime.now())
                .build();

        List<Price> prices = List.of(price);

        PriceDto expected = PriceDto.builder()
                .currencyFromCode(price.getInstrument().getFrom().getCode())
                .currencyToCode(price.getInstrument().getTo().getCode())
                .buyRate(price.getAskRate() + (price.getAskRate() * this.commissionRate))
                .sellRate(price.getBidRate() - (price.getBidRate() * this.commissionRate))
                .timestamp(LocalDateTime.now())
                .build();

        // parameters
        Instrument instrument = price.getInstrument();
        LocalDateTime timestamp = price.getTimestamp();

        // mocking
        Mockito.when(this.priceRepository.getAllPrices()).thenReturn(prices);

        // method invoke
        PriceDto actual = this.priceService.getLatestPrice(instrument, timestamp);

        // verification
        Mockito.verify(this.priceRepository, Mockito.times(1)).getAllPrices();

        // assertions
        Assertions.assertNotNull(actual);

        Assertions.assertNotNull(actual.getCurrencyFromCode());
        Assertions.assertEquals(expected.getCurrencyFromCode(), actual.getCurrencyFromCode());

        Assertions.assertNotNull(actual.getCurrencyToCode());
        Assertions.assertEquals(expected.getCurrencyToCode(), actual.getCurrencyToCode());

        Assertions.assertNotNull(actual.getBuyRate());
        Assertions.assertEquals(expected.getBuyRate(), actual.getBuyRate());

        Assertions.assertNotNull(actual.getSellRate());
        Assertions.assertEquals(expected.getSellRate(), actual.getSellRate());

        Assertions.assertNotNull(actual.getTimestamp());
    }

    @Test
    void getLatestPrice_validInstrumentAndTimestamp_doesNotContainPrice_shouldThrowPriceNotExistException() {
        // data
        Price price = Price.builder()
                .id(1L)
                .instrument(Instrument.EUR_USD)
                .askRate(1.1D)
                .bidRate(1.1D)
                .timestamp(LocalDateTime.now())
                .build();

        List<Price> prices = List.of(price);

        PriceDto expected = PriceDto.builder()
                .currencyFromCode(price.getInstrument().getFrom().getCode())
                .currencyToCode(price.getInstrument().getTo().getCode())
                .buyRate(price.getAskRate() + (price.getAskRate() * this.commissionRate))
                .sellRate(price.getBidRate() - (price.getBidRate() * this.commissionRate))
                .timestamp(LocalDateTime.now())
                .build();

        // parameters
        Instrument instrument = price.getInstrument();
        LocalDateTime timestamp = price.getTimestamp();

        // mocking
        Mockito.when(this.priceRepository.getAllPrices()).thenReturn(new ArrayList<>());

        // method invoke
        Assertions.assertThrows(PriceNotExistException.class, () -> this.priceService.getLatestPrice(instrument, timestamp));

        // verification
        Mockito.verify(this.priceRepository, Mockito.times(1)).getAllPrices();
    }
}
