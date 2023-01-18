package tr.unvercanunlu.fxmarket.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import tr.unvercanunlu.fxmarket.model.constant.Instrument;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class Price implements Serializable {

    private Long id;

    private Instrument instrument;

    private Double bidRate;

    private Double askRate;

    private LocalDateTime timestamp;
}
