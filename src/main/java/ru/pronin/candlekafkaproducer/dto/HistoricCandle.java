package ru.pronin.candlekafkaproducer.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.pronin.candlekafkaproducer.deserializer.HistoricalCandleDeserializer;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.TimeZone;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonDeserialize(using = HistoricalCandleDeserializer.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HistoricCandle {
    LocalDateTime openTime;
    BigDecimal openPrice;
    BigDecimal highPrice;
    BigDecimal lowPrice;
    BigDecimal closePrice;
    BigDecimal volume;
    LocalDateTime closeTime;
    BigDecimal quoteAssetVolume;
    int numberOfTrades;
    BigDecimal takerBaseAssetVolume;
    BigDecimal takerQuoteAssetVolume;

    public HistoricCandle(long openTime, BigDecimal openPrice, BigDecimal highPrice, BigDecimal lowPrice, BigDecimal closePrice, BigDecimal volume, long closeTime, BigDecimal quoteAssetVolume, int numberOfTrades, BigDecimal takerBaseAssetVolume, BigDecimal takerQuoteAssetVolume) {
        this.openTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(openTime), TimeZone.getDefault().toZoneId());
        this.openPrice = openPrice;
        this.highPrice = highPrice;
        this.lowPrice = lowPrice;
        this.closePrice = closePrice;
        this.volume = volume;
        this.closeTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(closeTime), TimeZone.getDefault().toZoneId());;
        this.quoteAssetVolume = quoteAssetVolume;
        this.numberOfTrades = numberOfTrades;
        this.takerBaseAssetVolume = takerBaseAssetVolume;
        this.takerQuoteAssetVolume = takerQuoteAssetVolume;
    }

    static public HistoricCandle getCandleWithNewCloseValue(HistoricCandle historicCandle, BigDecimal value) {
        return new HistoricCandle(
                Timestamp.valueOf(historicCandle.getOpenTime()).getTime(),
                historicCandle.getOpenPrice(),
                historicCandle.getHighPrice(),
                historicCandle.getLowPrice(),
                value,
                historicCandle.getVolume(),
                Timestamp.valueOf(historicCandle.getCloseTime()).getTime(),
                historicCandle.getQuoteAssetVolume(),
                historicCandle.getNumberOfTrades(),
                historicCandle.getTakerBaseAssetVolume(),
                historicCandle.getTakerQuoteAssetVolume()
        );
    }
}

