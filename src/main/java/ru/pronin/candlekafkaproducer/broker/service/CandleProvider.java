package ru.pronin.candlekafkaproducer.broker.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.pronin.candlekafkaproducer.broker.tinkoff.TinkoffImpl;
import ru.pronin.candlekafkaproducer.dto.Candle;
import ru.tinkoff.piapi.contract.v1.CandleInterval;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static ru.pronin.candlekafkaproducer.enums.CandleInterval.fromTinkoffInterval;

@Service
@RequiredArgsConstructor
public class CandleProvider {

    private final TinkoffImpl broker;

    @Value("${hours-to-subtract}")
    private Long hoursToSubtract;

    public List<Candle> getCandles(String figi, int count, CandleInterval interval) {
        List<Candle> candles = new ArrayList<>();
        Instant cur = Instant.now().minus(1, ChronoUnit.DAYS);
        while (count > candles.size()) {
            Instant from = getFrom(cur);
            Instant to = getTo(cur);
            candles.addAll(
                    broker.getMarketDataService()
                            .getCandlesSync(
                                    figi,
                                    from,
                                    to,
                                    interval)
                            .stream()
                            .map(historic -> new Candle(historic, fromTinkoffInterval(interval), hoursToSubtract))
                            .toList());
            cur = cur.minus(1, ChronoUnit.DAYS);
        }
        return candles.stream().skip(candles.size() - count).toList();
    }

    private Instant getTo(Instant from) {
        LocalDateTime cur = LocalDateTime.ofInstant(from, ZoneId.of("Europe/Moscow"));
        if (cur.getDayOfWeek().getValue() >= 6) {
            cur = cur.truncatedTo(ChronoUnit.DAYS)
                    .minusDays(cur.getDayOfWeek().getValue() - 5)
                    .plusHours(18L).plusMinutes(40L);
            return cur.toInstant(ZoneOffset.UTC);
        }
        if ((cur.getHour() == 18 && cur.getMinute() > 40) || cur.getHour() > 18) {
            cur = cur
                    .truncatedTo(ChronoUnit.DAYS)
                    .plusHours(18L).plusMinutes(40L);
            return cur.toInstant(ZoneOffset.UTC);
        }
        return from;
    }

    private Instant getFrom(Instant from) {
        LocalDateTime cur = LocalDateTime.ofInstant(from, ZoneId.of("Europe/Moscow"));
        if (cur.getDayOfWeek().getValue() >= 6) {
            cur = cur.minusDays(cur.getDayOfWeek().getValue() - 5);
        }
        if (cur.getHour() < 10) {
            cur = cur.minusDays(cur.getDayOfWeek().getValue() - 1);
        }
        cur = cur.truncatedTo(ChronoUnit.DAYS).plusHours(10L);
        return cur.toInstant(ZoneOffset.UTC);
    }
}
