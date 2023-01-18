package tr.unvercanunlu.fxmarket.controller.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import tr.unvercanunlu.fxmarket.config.ApiConfig;
import tr.unvercanunlu.fxmarket.error.exception.PriceNotExistException;
import tr.unvercanunlu.fxmarket.model.Price;
import tr.unvercanunlu.fxmarket.model.constant.Currency;
import tr.unvercanunlu.fxmarket.model.constant.Instrument;
import tr.unvercanunlu.fxmarket.model.response.PriceDto;
import tr.unvercanunlu.fxmarket.service.IPriceService;

import java.time.LocalDateTime;

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
    void getLatestPrice_validInstrument_withPrice_shouldReturnPrice() {
        // data
        Price price = Price.builder()
                .id(1L)
                .instrument(Instrument.EUR_USD)
                .ask(1D)
                .bid(1D)
                .timestamp(LocalDateTime.now())
                .build();

        PriceDto priceDto = PriceDto.builder()
                .currencyFromCode(price.getInstrument().getFrom().getCode())
                .currencyToCode(price.getInstrument().getTo().getCode())
                .buyRate(price.getAsk())
                .sellRate(price.getBid())
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
                            get(ApiConfig.PRICE_API + "/" + currencyCodeFrom + "/" + currencyCodeTo))
                    .andDo(print())
                    .andExpect(status().is(HttpStatus.OK.value()))
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(content().json(this.objectMapper.writeValueAsString(priceDto)));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
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
    void getLatestPrice_validInstrument_withoutPrice_shouldReturnNotFound() {
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
                            get(ApiConfig.PRICE_API + "/" + currencyCodeFrom + "/" + currencyCodeTo))
                    .andDo(print())
                    .andExpect(status().is(HttpStatus.NOT_FOUND.value()));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
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
    void getLatestPrice_notExistInstrument_shouldReturnBadRequest() {
        // parameters
        String currencyCodeFrom = Currency.JPY.getCode();
        String currencyCodeTo = Currency.USD.getCode();

        // rest call
        try {
            this.mockMvc.perform(
                            get(ApiConfig.PRICE_API + "/" + currencyCodeFrom + "/" + currencyCodeTo))
                    .andDo(print())
                    .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // verify
        verify(this.priceService, never()).getLatestPrice(any(), any());
    }

    @Test
    void getLatestPrice_notExistCurrencyCodeFrom_shouldReturnBadRequest() {
        // parameters
        String currencyCodeFrom = "TRY"; // Turkish Lira
        String currencyCodeTo = Currency.EUR.getCode();

        // rest call
        try {
            this.mockMvc.perform(
                            get(ApiConfig.PRICE_API + "/" + currencyCodeFrom + "/" + currencyCodeTo))
                    .andDo(print())
                    .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // verify
        verify(this.priceService, never()).getLatestPrice(any(), any());
    }

    @Test
    void getLatestPrice_notExistCurrencyCodeTo_shouldReturnBadRequest() {
        // parameters
        String currencyCodeFrom = Currency.USD.getCode();
        String currencyCodeTo = "TRY"; // Turkish Lira

        // rest call
        try {
            this.mockMvc.perform(
                            get(ApiConfig.PRICE_API + "/" + currencyCodeFrom + "/" + currencyCodeTo))
                    .andDo(print())
                    .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // verify
        verify(this.priceService, never()).getLatestPrice(any(), any());
    }

    @Test
    void getLatestPrice_nullCurrencyCodeFrom_shouldReturnBadRequest() {
        // parameters
        String currencyCodeFrom = null;
        String currencyCodeTo = Currency.USD.getCode();

        // rest call
        try {
            this.mockMvc.perform(
                            get(ApiConfig.PRICE_API + "/" + currencyCodeFrom + "/" + currencyCodeTo))
                    .andDo(print())
                    .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // verify
        verify(this.priceService, never()).getLatestPrice(any(), any());
    }

    @Test
    void getLatestPrice_nullCurrencyCodeTo_shouldReturnBadRequest() {
        // parameters
        String currencyCodeFrom = Currency.USD.getCode();
        String currencyCodeTo = null;

        // rest call
        try {
            this.mockMvc.perform(
                            get(ApiConfig.PRICE_API + "/" + currencyCodeFrom + "/" + currencyCodeTo))
                    .andDo(print())
                    .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // verify
        verify(this.priceService, never()).getLatestPrice(any(), any());
    }

    @Test
    void getLatestPrice_inValidCurrencyCodeFrom_shouldReturnBadRequest() {
        // parameters
        String currencyCodeFrom = "a";
        String currencyCodeTo = Currency.USD.getCode();

        // rest call
        try {
            this.mockMvc.perform(
                            get(ApiConfig.PRICE_API + "/" + currencyCodeFrom + "/" + currencyCodeTo))
                    .andDo(print())
                    .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // verify
        verify(this.priceService, never()).getLatestPrice(any(), any());
    }

    @Test
    void getLatestPrice_inValidCurrencyCodeTo_shouldReturnBadRequest() {
        // parameters
        String currencyCodeFrom = Currency.USD.getCode();
        String currencyCodeTo = "a";

        // rest call
        try {
            this.mockMvc.perform(
                            get(ApiConfig.PRICE_API + "/" + currencyCodeFrom + "/" + currencyCodeTo))
                    .andDo(print())
                    .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // verify
        verify(this.priceService, never()).getLatestPrice(any(), any());
    }
}
