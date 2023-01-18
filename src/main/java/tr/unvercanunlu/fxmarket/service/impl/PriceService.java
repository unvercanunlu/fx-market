package tr.unvercanunlu.fxmarket.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tr.unvercanunlu.fxmarket.error.exception.PriceNotExistException;
import tr.unvercanunlu.fxmarket.model.Price;
import tr.unvercanunlu.fxmarket.model.constant.Instrument;
import tr.unvercanunlu.fxmarket.model.response.PriceDto;
import tr.unvercanunlu.fxmarket.repository.IPriceRepository;
import tr.unvercanunlu.fxmarket.service.IPriceService;

import java.time.LocalDateTime;
import java.util.Comparator;

@Service
public class PriceService implements IPriceService {

    private final IPriceRepository priceRepository;

    @Autowired
    public PriceService(IPriceRepository priceRepository) {
        this.priceRepository = priceRepository;
    }

    @Override
    public PriceDto getLatestPrice(Instrument instrument, LocalDateTime timestamp) {
        Price price = this.priceRepository.getAllPrices()
                .stream()
                .filter(p -> p.getInstrument().equals(instrument))
                .sorted(Comparator.comparing(Price::getTimestamp).reversed())
                .filter(p -> !p.getTimestamp().isAfter(timestamp))
                .findFirst()
                .orElseThrow(() -> new PriceNotExistException(instrument, timestamp));

        return PriceDto.builder()
                .currencyFromCode(instrument.getFrom().getCode())
                .currencyToCode(instrument.getTo().getCode())
                .sellRate(price.getBid())
                .buyRate(price.getAsk())
                .timestamp(timestamp)
                .build();
    }
}
