package ru.pronin.candlekafkaproducer.dto;

import java.util.Arrays;

public enum OrderDirection {
    BUY, SELL;

    public static OrderDirection findByName(String name) {
        return Arrays.stream(values()).filter(d -> d.name().equals(name)).findFirst().orElse(SELL);
    }
}
