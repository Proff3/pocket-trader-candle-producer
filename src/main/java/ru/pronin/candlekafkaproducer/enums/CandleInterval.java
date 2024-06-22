package ru.pronin.candlekafkaproducer.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static ru.tinkoff.piapi.contract.v1.CandleInterval.*;

@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public enum CandleInterval {

    ONE_MINUTE("1m", IntStream.range(0, 60).boxed().collect(Collectors.toList()), CANDLE_INTERVAL_1_MIN, 1),
    THREE_MINUTES("3m", List.of(0, 3, 6, 9, 12, 15, 18, 21, 24, 27, 30, 33, 36, 39, 42, 45, 48, 51, 54, 57)),
    FIVE_MINUTES("5m", List.of(0, 5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55), CANDLE_INTERVAL_5_MIN, 5),
    FIFTEEN_MINUTES("15m", List.of(0, 15, 30, 45), CANDLE_INTERVAL_15_MIN, 15),
    HALF_HOURLY("30m", List.of(0, 30)),
    HOURLY("1h", List.of(0), CANDLE_INTERVAL_HOUR, 60),
    TWO_HOURLY("2h", List.of()),
    FOUR_HOURLY("4h", List.of()),
    SIX_HOURLY("6h", List.of()),
    EIGHT_HOURLY("8h", List.of()),
    TWELVE_HOURLY("12h", List.of()),
    DAILY("1d", List.of(), CANDLE_INTERVAL_DAY, 60 * 24),
    THREE_DAILY("3d", List.of()),
    WEEKLY("1w", List.of()),
    MONTHLY_MARKET("1M", List.of()),
    MONTHLY_VISION("1mo", List.of());

    private static final Map<ru.tinkoff.piapi.contract.v1.CandleInterval, CandleInterval> TINKOFF_INTERVALS =
            Arrays.stream(values()).filter(interval -> interval.getTinkoffInterval() != null).collect(Collectors.toMap(CandleInterval::getTinkoffInterval, Function.identity()));

    private final String value;
    private final List<Integer> minutesRange;
    private ru.tinkoff.piapi.contract.v1.CandleInterval tinkoffInterval;
    private int minutes;

    public String toString() {
        return this.value;
    }

    public static CandleInterval fromValue(String value) {
        CandleInterval[] var1 = values();
        int var2 = var1.length;

        for (int var3 = 0; var3 < var2; ++var3) {
            CandleInterval e = var1[var3];
            if (e.toString().equals(value)) {
                return e;
            }
        }

        return null;
    }

    public static CandleInterval fromTinkoffInterval(ru.tinkoff.piapi.contract.v1.CandleInterval interval) {
        return TINKOFF_INTERVALS.getOrDefault(interval, FIVE_MINUTES);
    }

    public static long getMinutesRange(CandleInterval interval) {
        switch (interval) {
            case ONE_MINUTE: {
                return 1L;
            }
            case THREE_MINUTES: {
                return 3L;
            }
            case FIVE_MINUTES: {
                return 5L;
            }
            case FIFTEEN_MINUTES: {
                return 15L;
            }
            case HALF_HOURLY: {
                return 30L;
            }
            case HOURLY: {
                return 60L;
            }
            case TWO_HOURLY: {
                return 120L;
            }
            case FOUR_HOURLY: {
                return 240L;
            }
            case SIX_HOURLY: {
                return 360L;
            }
            case EIGHT_HOURLY: {
                return 480L;
            }
            case TWELVE_HOURLY: {
                return 720L;
            }
            case DAILY: {
                return 1440L;
            }
            case THREE_DAILY: {
                return 4320L;
            }
            case WEEKLY: {
                return 10080L;
            }
            case MONTHLY_MARKET:
            case MONTHLY_VISION: {
                return 43200L;
            }
            default:
                throw new IllegalStateException("Unexpected value: " + interval);
        }
    }
}
