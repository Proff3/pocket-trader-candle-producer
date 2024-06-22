package ru.pronin.candlekafkaproducer.broker.stream;

import ru.pronin.candlekafkaproducer.dto.request.CandleTime;
import ru.pronin.candlekafkaproducer.enums.Share;

import java.util.List;
import java.util.Map;

import static java.util.Objects.isNull;

public interface CandleProducerFactory {

    void createProducer(Share share, CandleTime candleTime);

    Map<Share, List<CandleTime>> getStreamIds();

    CandleProducerFactoryType getType();

    default void deleteProducer(Share share, CandleTime candleTime) {
        Map<Share, List<CandleTime>> streamIds = getStreamIds();
        if (isNull(streamIds)) return;

        if (streamIds.isEmpty()) {
            return;
        }

        if (streamIds.containsKey(share) && streamIds.get(share).contains(candleTime)) {
            if (streamIds.get(share).size() == 1) {
                streamIds.get(share);
            } else {
                streamIds.get(share).remove(candleTime);
            }
        }
    }
}
