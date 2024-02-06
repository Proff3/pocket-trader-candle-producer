package ru.pronin.candlekafkaproducer.broker.stream;

import ru.pronin.candlekafkaproducer.dto.request.CandleTime;
import ru.pronin.candlekafkaproducer.enums.Share;

import java.util.Map;

public interface CandleProducerFactory {
    String createProducer(Share share, CandleTime candleTime);
    Map<String, String> getStreamIds();
    CandleProducerFactoryType getType();
    default void deleteProducer(Share share, CandleTime candleTime) {
        String key = getKey(share, candleTime);
        getStreamIds().remove(key);
    }
    default String getKey(Share share, CandleTime candleTime) {
        return share.getFigi() + candleTime.getCount();
    }
}
