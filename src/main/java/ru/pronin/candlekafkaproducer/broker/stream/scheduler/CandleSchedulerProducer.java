package ru.pronin.candlekafkaproducer.broker.stream.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.pronin.candlekafkaproducer.dto.request.CandleTime;
import ru.pronin.candlekafkaproducer.enums.Share;
import ru.pronin.candlekafkaproducer.broker.tinkoff.TinkoffImpl;
import ru.pronin.candlekafkaproducer.dto.Candle;
import ru.pronin.candlekafkaproducer.enums.TimeZone;
import ru.tinkoff.piapi.contract.v1.HistoricCandle;
import ru.tinkoff.piapi.core.MarketDataService;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static java.time.ZoneOffset.UTC;
import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

@Slf4j
@Service
@Scope(value = SCOPE_PROTOTYPE)
@EnableScheduling
@RequiredArgsConstructor
public class CandleSchedulerProducer {

    private final TinkoffImpl broker;
    private final KafkaTemplate<String, Candle> candleKafkaTemplate;
    private Share share;
    private CandleTime candleTime;

    @Value("${hours-to-subtract}")
    private Long hoursToSubtract;

    public void init(Share share, CandleTime candleTime) {
        this.share = share;
        this.candleTime = candleTime;
    }

    @Scheduled(cron = "0 * * * * *")
    public void createCandleStream() {
        if (share != null) {
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
                    .thenAccept(candles -> processCandles(to, candles));
        }
    }

    private void processCandles(Instant to, List<HistoricCandle> historicCandles) {
        int idx = LocalDateTime.ofInstant(to, UTC).minusHours(hoursToSubtract).getMinute() % candleTime.getMinutes() == 0 ? 0 : 1;
        HistoricCandle historicCandle = historicCandles.get(idx);
        Candle candle = new Candle(historicCandle, candleTime.getInterval(), hoursToSubtract);
        candleKafkaTemplate.send(share.getFigi(), candle.getCurrentTime().toString(), candle);
        log.info("Send candle {}", historicCandle);
    }
}
