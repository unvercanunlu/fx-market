package tr.unvercanunlu.fxmarket.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import tr.unvercanunlu.fxmarket.model.response.PriceDto;
import tr.unvercanunlu.fxmarket.validation.annotation.CurrencyCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Validated
public interface IPriceRestController {

    ResponseEntity<PriceDto> getLatestPrice(
            @NotNull(message = "Currency from code must be not null.")
            @NotBlank(message = "Currency from code must be not blank.")
            @Size(min = 3, max = 3, message = "Currency from code must be 3 characters long.")
            @CurrencyCode(message = "Currency from code is not available.")
            String currencyFromCode,
            @NotNull(message = "Currency to code must be not null.")
            @NotBlank(message = "Currency to code must be not blank.")
            @Size(min = 3, max = 3, message = "Currency to code must be 3 characters long.")
            @CurrencyCode(message = "Currency to code is not available.")
            String currencyToCode
    );
}
