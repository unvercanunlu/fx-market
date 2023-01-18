package tr.unvercanunlu.fxmarket.repository;

import org.springframework.validation.annotation.Validated;
import tr.unvercanunlu.fxmarket.model.Price;

import javax.validation.constraints.NotNull;
import java.util.List;

@Validated
public interface IPriceRepository {

    List<Price> getAllPrices();

    void add(@NotNull(message = "Price should not be null.") Price price);
}
