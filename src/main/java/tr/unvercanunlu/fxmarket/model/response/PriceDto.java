package tr.unvercanunlu.fxmarket.model.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class PriceDto implements Serializable {

    private String currencyFromCode;

    private String currencyToCode;

    private Double buyRate;

    private Double sellRate;

    private LocalDateTime timestamp;
}
