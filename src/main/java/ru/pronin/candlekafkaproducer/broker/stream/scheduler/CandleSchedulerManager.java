package ru.pronin.candlekafkaproducer.broker.stream.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.pronin.candlekafkaproducer.broker.stream.CandleProducerFactory;
import ru.pronin.candlekafkaproducer.broker.stream.CandleProducerFactoryType;
import ru.pronin.candlekafkaproducer.broker.tinkoff.TinkoffImpl;
import ru.pronin.candlekafkaproducer.dto.Candle;
import ru.pronin.candlekafkaproducer.dto.CandleDto;
import ru.pronin.candlekafkaproducer.dto.request.CandleTime;
import ru.pronin.candlekafkaproducer.enums.Share;
import ru.pronin.candlekafkaproducer.enums.TimeZone;
import ru.tinkoff.piapi.contract.v1.HistoricCandle;
import ru.tinkoff.piapi.core.MarketDataService;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static java.time.ZoneOffset.UTC;
import static ru.pronin.candlekafkaproducer.dto.request.CandleTime.MIN_5;

@Slf4j
@Service
@RequiredArgsConstructor
public class CandleSchedulerManager implements CandleProducerFactory {

    private final TinkoffImpl broker;
    private final KafkaTemplate<String, CandleDto> candleKafkaTemplate;
    private final Map<Share, List<CandleTime>> streams = new ConcurrentHashMap<>();

    @Value("${hours-to-subtract}")
    private Long hoursToSubtract;
    @Value("${topic-name}")
    private String topicName;

    @Override
    public void createProducer(Share share, CandleTime candleTime) {
        if (streams.containsKey(share) && streams.get(share).contains(candleTime)) {
            return;
        }
        if (!streams.containsKey(share)) {
            streams.put(share, Collections.synchronizedList(new ArrayList<>()));
        }
        streams.get(share).add(candleTime);
        log.info("Create scheduler {} with interval {}", share.getName(), candleTime);
    }

    @Scheduled(cron = "0 * * * * *")
    public void createCandleStream() {
        streams.forEach((share, times) ->
           times.forEach(candleTime -> {
               LocalDateTime now = LocalDateTime.ofInstant(Instant.now(), UTC).minusHours(hoursToSubtract);
               TimeZone timeZone = share.getTimeZone();
               if (timeZone.getStartHours() > now.getHour() || now.getHour() > timeZone.getEndHours()
                       || (timeZone.getStartHours() == now.getHour() && timeZone.getStartMinutes() > now.getMinute())
                       || (timeZone.getEndHours() == now.getHour() && now.getMinute() > timeZone.getEndMinutes())) {
                   return;
               }
               MarketDataService marketDataService = broker.getMarketDataService();
               Instant from = Instant.now().minus(hoursToSubtract, ChronoUnit.HOURS).truncatedTo(ChronoUnit.SECONDS).minus(candleTime.getMinutes(), ChronoUnit.MINUTES);
               Instant to = Instant.now().minus(hoursToSubtract, ChronoUnit.HOURS).truncatedTo(ChronoUnit.SECONDS);
               marketDataService
                       .getCandles(share.getFigi(), from, to, candleTime.getInterval().getTinkoffInterval())
                       .thenAccept(candles -> processCandles(to, share, candleTime, candles));
           })
        );
    }

    private void processCandles(Instant to, Share share, CandleTime candleTime, List<HistoricCandle> historicCandles) {
        int idx = LocalDateTime.ofInstant(to, UTC).minusHours(hoursToSubtract).getMinute() % candleTime.getMinutes() == 0 ? 0 : 1;
        var historicCandle = historicCandles.get(idx);
        var candle = new Candle(historicCandle, candleTime.getInterval(), hoursToSubtract);
        var candleDto = new CandleDto(share.getFigi(), candle);
        candleKafkaTemplate.send(topicName, UUID.randomUUID().toString(), candleDto);
        log.info("Send candle {}", candleDto);
    }

    @Override
    public Map<Share, List<CandleTime>> getStreamIds() {
        return streams;
    }

    @Override
    public CandleProducerFactoryType getType() {
        return CandleProducerFactoryType.SCHEDULER;
    }
}
