package ru.pronin.candlekafkaproducer.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Data;
import ru.pronin.candlekafkaproducer.enums.ChartInterval;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.TimeZone;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class StreamCandle {

    @JsonProperty("t")
    LocalDateTime openTime;
    @JsonProperty("T")
    LocalDateTime closeTime;
    @JsonProperty("s")
    String symbol;
    @JsonProperty("i")
    ChartInterval interval;
    @JsonProperty("f")
    BigDecimal firstTradeId;
    @JsonProperty("L")
    BigDecimal lastTradeId;
    @JsonProperty("o")
    BigDecimal openPrice;
    @JsonProperty("h")
    BigDecimal highPrice;
    @JsonProperty("l")
    BigDecimal lowPrice;
    @JsonProperty("c")
    BigDecimal closePrice;
    @JsonProperty("v")
    BigDecimal baseVolume;
    @JsonProperty("q")
    BigDecimal assetVolume;
    @JsonProperty("x")
    boolean isClosed;
    @JsonProperty("V")
    BigDecimal takerBaseAssetVolume;
    @JsonProperty("Q")
    BigDecimal takerQuoteAssetVolume;

    @JsonSetter("t")
    public void setTheOpenTime(long timestamp) {
        this.openTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), TimeZone.getDefault().toZoneId());;
    }

    @JsonSetter("T")
    public void setTheCloseTime(long timestamp) {
        this.closeTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), TimeZone.getDefault().toZoneId());;
    }

    @JsonSetter("i")
    public void setTheChartInterval(String chartInterval) {
        this.interval = ChartInterval.fromValue(chartInterval);
    }
}
