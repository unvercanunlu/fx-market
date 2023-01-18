package tr.unvercanunlu.fxmarket.mapper;

import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;

@Validated
public interface IMapper<A, B> {

    B map(@NotNull(message = "Mapper from should not be null") A from);
}
