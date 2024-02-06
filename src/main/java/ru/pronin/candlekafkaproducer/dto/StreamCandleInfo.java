package ru.pronin.candlekafkaproducer.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.TimeZone;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class StreamCandleInfo {

    @JsonProperty("k")
    private StreamCandle streamCandle;
    @JsonProperty("E")
    private LocalDateTime eventTime;
    @JsonProperty("e")
    private String event;
    @JsonProperty("s")
    private String symbol;

    @JsonSetter("E")
    public void setTheCloseTime(long timestamp) {
        this.eventTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), TimeZone.getDefault().toZoneId());;
    }
}
