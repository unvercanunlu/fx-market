package tr.unvercanunlu.fxmarket.service;

import org.springframework.validation.annotation.Validated;
import tr.unvercanunlu.fxmarket.model.constant.Instrument;
import tr.unvercanunlu.fxmarket.model.response.PriceDto;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Validated
public interface IPriceService {

    PriceDto getLatestPrice(
            @NotNull(message = "Instrument must be not null.") Instrument instrument,
            @NotNull(message = "Timestamp must be not null.") LocalDateTime timestamp
    );
}
