package tr.unvercanunlu.fxmarket.repository.impl;

import org.springframework.stereotype.Repository;
import tr.unvercanunlu.fxmarket.model.Price;
import tr.unvercanunlu.fxmarket.repository.IPriceRepository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class PriceInMemoryRepository implements IPriceRepository {

    private static final List<Price> prices = new ArrayList<>();

    @Override
    public List<Price> getAllPrices() {
        return prices;
    }

    @Override
    public void add(Price price) {
        prices.add(price);
    }
}
