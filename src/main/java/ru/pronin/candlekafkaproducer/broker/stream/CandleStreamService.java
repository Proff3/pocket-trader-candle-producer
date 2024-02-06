package ru.pronin.candlekafkaproducer.broker.stream;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.pronin.candlekafkaproducer.dto.request.CandleTime;
import ru.pronin.candlekafkaproducer.enums.Share;
import ru.pronin.candlekafkaproducer.enums.CandleInterval;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CandleStreamService {
    private final Map<CandleProducerFactoryType, CandleProducerFactory> factories;

    @Autowired
    public CandleStreamService(List<CandleProducerFactory> factories) {
        this.factories = factories.stream().collect(Collectors.toMap(CandleProducerFactory::getType, Function.identity()));
    }

    public String createProducer(Share share, CandleTime candleTime) {
        log.info("Creating producer");
        return factories.get(CandleProducerFactoryType.SCHEDULER).createProducer(share, candleTime);
    }

    public void deleteProducer(Share share, CandleTime candleTime) {
        log.info("Deleting producer");
        factories.get(CandleProducerFactoryType.SCHEDULER).deleteProducer(share, candleTime);
    }
}
