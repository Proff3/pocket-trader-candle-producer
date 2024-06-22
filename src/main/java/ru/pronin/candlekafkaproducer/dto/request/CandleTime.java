package ru.pronin.candlekafkaproducer.dto.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.pronin.candlekafkaproducer.enums.CandleInterval;

import static ru.pronin.candlekafkaproducer.enums.CandleInterval.*;

@Getter
@RequiredArgsConstructor
public enum CandleTime {

    MIN_1(60L * 1000L, ONE_MINUTE, 1),
    MIN_5(MIN_1.count * 5L, FIVE_MINUTES,5),
    MIN_15(MIN_1.count * 15L, FIFTEEN_MINUTES, 15),
    HOUR(MIN_1.count * 60L, HOURLY, 60),
    DAY(HOUR.count * 24L, DAILY, 60 * 24),
    ;

    private final long count;
    private final CandleInterval interval;
    private final int minutes;
}
