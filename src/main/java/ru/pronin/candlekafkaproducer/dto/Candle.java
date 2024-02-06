package ru.pronin.candlekafkaproducer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;
import ru.pronin.candlekafkaproducer.enums.CandleInterval;
import ru.tinkoff.piapi.contract.v1.Quotation;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
@With
public class Candle {

    private LocalDateTime openTime;
    private BigDecimal openPrice;
    private BigDecimal highPrice;
    private BigDecimal lowPrice;
    private BigDecimal closePrice;
    private BigDecimal volume;
    private LocalDateTime closeTime;
    private LocalDateTime currentTime;

    public Candle(HistoricCandle candle) {
        this.setClosePrice(candle.getClosePrice());
        this.setHighPrice(candle.getHighPrice());
        this.setLowPrice(candle.getLowPrice());
        this.setOpenPrice(candle.getOpenPrice());
        this.setOpenTime(candle.getOpenTime());
        this.setCloseTime(candle.getCloseTime());
        this.setVolume(candle.getVolume());
    }

    public Candle(StreamCandleInfo candleInfo) {
        StreamCandle candle = candleInfo.getStreamCandle();
        this.setClosePrice(candle.getClosePrice());
        this.setHighPrice(candle.getHighPrice());
        this.setLowPrice(candle.getLowPrice());
        this.setOpenPrice(candle.getOpenPrice());
        this.setOpenTime(candle.getOpenTime());
        this.setVolume(candle.getBaseVolume());
        this.setCloseTime(candle.getCloseTime());
        this.setCurrentTime(candleInfo.getEventTime());
    }

    public Candle(ru.tinkoff.piapi.contract.v1.HistoricCandle historicCandle) {
        this.setClosePrice(getFromQuotation(historicCandle.getClose()));
        this.setHighPrice(getFromQuotation(historicCandle.getHigh()));
        this.setLowPrice(getFromQuotation(historicCandle.getLow()));
        this.setOpenPrice(getFromQuotation(historicCandle.getOpen()));
        this.setOpenTime(LocalDateTime.ofEpochSecond(historicCandle.getTime().getSeconds(), 0, ZoneOffset.UTC));
        this.setVolume(BigDecimal.valueOf(historicCandle.getVolume()));
    }

    public Candle(ru.tinkoff.piapi.contract.v1.HistoricCandle historicCandle, CandleInterval interval, long hoursToSubtract) {
        this.setClosePrice(getFromQuotation(historicCandle.getClose()));
        this.setHighPrice(getFromQuotation(historicCandle.getHigh()));
        this.setLowPrice(getFromQuotation(historicCandle.getLow()));
        this.setOpenPrice(getFromQuotation(historicCandle.getOpen()));
        this.setOpenTime(LocalDateTime.ofEpochSecond(historicCandle.getTime().getSeconds(), 0, ZoneOffset.UTC));
        this.setVolume(BigDecimal.valueOf(historicCandle.getVolume()));
        this.setCurrentTime(LocalDateTime.now().minus(hoursToSubtract, ChronoUnit.HOURS));
        this.setCloseTime(getOpenTime().plusMinutes(CandleInterval.getMinutesRange(interval)));
    }

    public Candle(ru.tinkoff.piapi.contract.v1.Candle candle, CandleInterval interval) {
        this.setClosePrice(getFromQuotation(candle.getClose()));
        this.setHighPrice(getFromQuotation(candle.getHigh()));
        this.setLowPrice(getFromQuotation(candle.getLow()));
        this.setOpenPrice(getFromQuotation(candle.getOpen()));
        this.setOpenTime(LocalDateTime.ofEpochSecond(candle.getTime().getSeconds(), 0, ZoneOffset.UTC));
        this.setVolume(BigDecimal.valueOf(candle.getVolume()));
        this.setCurrentTime(LocalDateTime.ofEpochSecond(candle.getLastTradeTs().getSeconds(), 0, ZoneOffset.UTC));
        this.setCloseTime(getOpenTime().plusMinutes(CandleInterval.getMinutesRange(interval)));
    }

    public static Candle getCandleWithNewCloseValue(Candle candle, BigDecimal value) {
        return new Candle(
                candle.getOpenTime(),
                candle.getOpenPrice(),
                candle.getHighPrice(),
                candle.getLowPrice(),
                value,
                candle.getVolume(),
                candle.getCloseTime(),
                candle.getOpenTime()
        );
    }

    private BigDecimal getFromQuotation(Quotation quotation) {
        BigDecimal nano = new BigDecimal(quotation.getNano());
        return new BigDecimal(quotation.getUnits()).add(nano.movePointLeft(9));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Candle candle = (Candle) o;
        return openTime.equals(candle.openTime) && openPrice.equals(candle.openPrice) && Objects.equals(highPrice, candle.highPrice) && Objects.equals(lowPrice, candle.lowPrice) && Objects.equals(closePrice, candle.closePrice) && Objects.equals(volume, candle.volume) && Objects.equals(closeTime, candle.closeTime) && Objects.equals(currentTime, candle.currentTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(openTime, openPrice);
    }
}
