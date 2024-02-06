package ru.pronin.candlekafkaproducer.enums;

public enum ChartInterval {

    ONE_SECOND("1s"),
    ONE_MINUTE("1m"),
    THREE_MINUTES("3m"),
    FIVE_MINUTES("5m"),
    FIFTEEN_MINUTES("15m"),
    HALF_HOURLY("30m"),
    HOURLY("1h"),
    TWO_HOURLY("2h"),
    FOUR_HOURLY("4h"),
    SIX_HOURLY("6h"),
    EIGHT_HOURLY("8h"),
    TWELVE_HOURLY("12h"),
    DAILY("1d"),
    THREE_DAILY("3d"),
    WEEKLY("1w"),
    MONTHLY_MARKET("1M"),
    ;

    final String value;

    private ChartInterval(String value) {
        this.value = value;
    }

    public String toString() {
        return this.value;
    }

    public static ChartInterval fromValue(String value) {
        ChartInterval[] var1 = values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            ChartInterval e = var1[var3];
            if (e.toString().equals(value)) {
                return e;
            }
        }

        return null;
    }
}
