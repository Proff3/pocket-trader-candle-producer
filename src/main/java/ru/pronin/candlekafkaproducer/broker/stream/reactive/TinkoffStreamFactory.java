package ru.pronin.candlekafkaproducer.broker.stream.reactive;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.pronin.candlekafkaproducer.broker.stream.CandleProducerFactory;
import ru.pronin.candlekafkaproducer.broker.stream.CandleProducerFactoryType;
import ru.pronin.candlekafkaproducer.dto.CandleDto;
import ru.pronin.candlekafkaproducer.dto.request.CandleTime;
import ru.pronin.candlekafkaproducer.enums.Share;
import ru.pronin.candlekafkaproducer.broker.tinkoff.TinkoffImpl;
import ru.pronin.candlekafkaproducer.dto.Candle;
import ru.pronin.candlekafkaproducer.enums.CandleInterval;
import ru.tinkoff.piapi.contract.v1.MarketDataResponse;
import ru.tinkoff.piapi.core.stream.MarketDataStreamService;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static ru.tinkoff.piapi.contract.v1.SubscriptionInterval.SUBSCRIPTION_INTERVAL_FIVE_MINUTES;

@Slf4j
@Service
public class TinkoffStreamFactory implements CandleProducerFactory {

    private final MarketDataStreamService marketDataStreamService;
    private final KafkaTemplate<String, CandleDto> candleKafkaTemplate;
    private final Map<String, String> streams = new ConcurrentHashMap<>();

    @Autowired
    public TinkoffStreamFactory(TinkoffImpl broker,
                                KafkaTemplate<String, CandleDto> candleKafkaTemplate) {
        this.marketDataStreamService = broker.getMarketDataStreamService();
        this.candleKafkaTemplate = candleKafkaTemplate;
    }

    @Override
    public void createProducer(Share share, CandleTime candleTime) {
//        String key = getKey(share, candleTime);
//        if (streams.containsKey(key)) {
//            return;
//        }
//        String id = UUID.randomUUID().toString();
//        String topic = share.getFigi();
//        log.info("Create");
//        marketDataStreamService
//                .newStream(
//                        id,
//                        data -> {
//                            try {
//                                if (MarketDataResponse.PayloadCase.CANDLE.equals(data.getPayloadCase())) {
//                                    Candle candle = new Candle(data.getCandle(), CandleInterval.FIVE_MINUTES);
//                                    log.info("Candle: {}", candle);
//                                    candleKafkaTemplate.send(topic, candle.getCurrentTime().toString(), candle);
//                                }
//                            } catch (Exception ex) {
//                                log.debug(data.toString());
//                                log.debug(ex.getMessage());
//                            }
//                        },
//                        ex -> log.error(ex.getMessage()))
//                .subscribeCandles(List.of(share.getFigi()), SUBSCRIPTION_INTERVAL_FIVE_MINUTES);
//        streams.put(key, id);
    }

    @Override
    public Map<Share, List<CandleTime>> getStreamIds() {
        return null;
    }

    @Override
    public CandleProducerFactoryType getType() {
        return CandleProducerFactoryType.REACTIVE;
    }
}
