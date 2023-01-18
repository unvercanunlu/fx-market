package tr.unvercanunlu.fxmarket.controller.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tr.unvercanunlu.fxmarket.config.FxMarketConfig;
import tr.unvercanunlu.fxmarket.error.exception.PriceNotExistException;
import tr.unvercanunlu.fxmarket.model.Price;
import tr.unvercanunlu.fxmarket.model.constant.Currency;
import tr.unvercanunlu.fxmarket.model.constant.Instrument;
import tr.unvercanunlu.fxmarket.model.response.PriceDto;
import tr.unvercanunlu.fxmarket.service.IPriceService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PriceRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IPriceService priceService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getLatestPrice_validParameters_containPrice_shouldReturnPrice() {
        // data
        Price price = Price.builder()
                .id(1L)
                .instrument(Instrument.EUR_USD)
                .askRate(1.1D)
                .bidRate(1.1D)
                .timestamp(LocalDateTime.now())
                .build();

        PriceDto priceDto = PriceDto.builder()
                .currencyFromCode(price.getInstrument().getFrom().getCode())
                .currencyToCode(price.getInstrument().getTo().getCode())
                .buyRate(price.getAskRate())
                .sellRate(price.getBidRate())
                .timestamp(LocalDateTime.now())
                .build();

        // parameters
        String currencyCodeFrom = price.getInstrument().getFrom().getCode();
        String currencyCodeTo = price.getInstrument().getTo().getCode();

        // mock
        when(this.priceService.getLatestPrice(
                any(Instrument.class), any(LocalDateTime.class))
        ).thenReturn(priceDto);

        // rest call
        try {
            this.mockMvc.perform(
                            get(FxMarketConfig.RestApi.PRICE + "/" + currencyCodeFrom + "/" + currencyCodeTo))
                    .andDo(print())
                    .andExpect(status().is(HttpStatus.OK.value()))
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(content().json(this.objectMapper.writeValueAsString(priceDto)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // argument captors
        ArgumentCaptor<Instrument> instrumentCaptor = ArgumentCaptor.forClass(Instrument.class);
        ArgumentCaptor<LocalDateTime> timestampCaptor = ArgumentCaptor.forClass(LocalDateTime.class);

        // verify
        verify(this.priceService, times(1))
                .getLatestPrice(instrumentCaptor.capture(), timestampCaptor.capture());

        // assertions
        assertNotNull(instrumentCaptor.getValue());
        assertEquals(instrumentCaptor.getValue(), price.getInstrument());

        assertNotNull(timestampCaptor.getValue());
    }

    @Test
    void getLatestPrice_validParameters_notContainPrice_shouldReturnNotFound() {
        // data
        Instrument instrument = Instrument.EUR_USD;
        LocalDateTime timestamp = LocalDateTime.now();
        PriceNotExistException exception = new PriceNotExistException(instrument, timestamp);

        // parameters
        String currencyCodeFrom = instrument.getFrom().getCode();
        String currencyCodeTo = instrument.getTo().getCode();

        // mock
        when(this.priceService.getLatestPrice(
                any(Instrument.class), any(LocalDateTime.class))
        ).thenThrow(exception);

        // rest call
        try {
            this.mockMvc.perform(
                            get(FxMarketConfig.RestApi.PRICE + "/" + currencyCodeFrom + "/" + currencyCodeTo))
                    .andDo(print())
                    .andExpect(status().is(HttpStatus.NOT_FOUND.value()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // argument captors
        ArgumentCaptor<Instrument> instrumentCaptor = ArgumentCaptor.forClass(Instrument.class);
        ArgumentCaptor<LocalDateTime> timestampCaptor = ArgumentCaptor.forClass(LocalDateTime.class);

        // verify
        verify(this.priceService, times(1))
                .getLatestPrice(instrumentCaptor.capture(), timestampCaptor.capture());

        // assertions
        assertNotNull(instrumentCaptor.getValue());
        assertEquals(instrumentCaptor.getValue(), instrument);

        assertNotNull(timestampCaptor.getValue());
    }

    @Test
    void getLatestPrice_inValidOrNotExistOrNullParameters_shouldReturnBadRequest() {
        List<Map<String, String>> testCases = List.of(
                // not exist instrument
                Map.of(
                        "currencyFromCode", Currency.JPY.getCode(),
                        "currencyToCode", Currency.USD.getCode()),
                // not exist currency from code
                Map.of(
                        "currencyFromCode", "TRY", // Turkish Lira
                        "currencyToCode", Currency.USD.getCode()),
                // not exist currency to code
                Map.of(
                        "currencyFromCode", Currency.USD.getCode(),
                        "currencyToCode", "TRY"), // Turkish Lira
                // invalid currency from code
                Map.of(
                        "currencyFromCode", "a",
                        "currencyToCode", Currency.USD.getCode()),
                // invalid currency to code
                Map.of(
                        "currencyFromCode", Currency.USD.getCode(),
                        "currencyToCode", "a"),
                // null currency from code
                Map.of(
                        "currencyFromCode", "null",
                        "currencyToCode", Currency.USD.getCode()),
                // null currency to code
                Map.of(
                        "currencyFromCode", Currency.USD.getCode(),
                        "currencyToCode", "null"));

        // variables
        String currencyFromCode, currencyToCode;

        for (Map<String, String> testCase : testCases) {
            // parameters
            currencyFromCode = testCase.get("currencyFromCode");
            currencyToCode = testCase.get("currencyToCode");

            // rest call
            try {
                this.mockMvc.perform(
                                get(FxMarketConfig.RestApi.PRICE + "/" + currencyFromCode + "/" + currencyToCode))
                        .andDo(print())
                        .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            // verify
            verify(this.priceService, never()).getLatestPrice(any(), any());
        }
    }
}
