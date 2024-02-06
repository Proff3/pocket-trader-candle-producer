package ru.pronin.candlekafkaproducer.dto.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.tinkoff.piapi.contract.v1.CandleInterval;

@Getter
@RequiredArgsConstructor
public enum CandleTime {

    MIN_1(60L * 1000L, CandleInterval.CANDLE_INTERVAL_1_MIN, 1),
    MIN_5(MIN_1.count * 5L, CandleInterval.CANDLE_INTERVAL_5_MIN, 5),
    MIN_15(MIN_1.count * 15L, CandleInterval.CANDLE_INTERVAL_15_MIN, 15),
    HOUR(MIN_1.count * 60L, CandleInterval.CANDLE_INTERVAL_HOUR, 60),
    DAY(HOUR.count * 24L, CandleInterval.CANDLE_INTERVAL_DAY, 60 * 24),
    ;

    private final long count;
    private final CandleInterval interval;
    private final int minutes;
}
