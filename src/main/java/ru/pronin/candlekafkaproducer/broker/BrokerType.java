package ru.pronin.candlekafkaproducer.broker;

public enum BrokerType {

    BINANCE("binance"),
    TINKOFF("tinkoff"),
    BYBIT("bybit"),
    ;

    private final String typeName;

    BrokerType(String typeName) {
        this.typeName = typeName;
    }

    public String getTypeName() {
        return typeName;
    }
}
