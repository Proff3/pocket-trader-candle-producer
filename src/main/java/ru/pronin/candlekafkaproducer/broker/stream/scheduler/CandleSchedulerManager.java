package ru.pronin.candlekafkaproducer.broker.stream.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.stereotype.Service;
import ru.pronin.candlekafkaproducer.broker.stream.CandleProducerFactory;
import ru.pronin.candlekafkaproducer.broker.stream.CandleProducerFactoryType;
import ru.pronin.candlekafkaproducer.dto.request.CandleTime;
import ru.pronin.candlekafkaproducer.enums.Share;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class CandleSchedulerManager implements CandleProducerFactory {

    private final ObjectFactory<CandleSchedulerProducer> candleSchedulerProducerFactory;
    private final Map<String, String> streams = new ConcurrentHashMap<>();

    @Override
    public String createProducer(Share share, CandleTime candleTime) {
        String key = share.getFigi() + candleTime.getCount();
        if (streams.containsKey(key)) {
            return streams.get(key);
        }
        CandleSchedulerProducer candleProducer = candleSchedulerProducerFactory.getObject();
        candleProducer.init(share, candleTime);
        streams.put(key, share.getFigi());
        log.info("Create scheduler {} with interval {}", share.getName(), candleTime);
        return share.getFigi();
    }

    @Override
    public Map<String, String> getStreamIds() {
        return streams;
    }

    @Override
    public CandleProducerFactoryType getType() {
        return CandleProducerFactoryType.SCHEDULER;
    }
}
