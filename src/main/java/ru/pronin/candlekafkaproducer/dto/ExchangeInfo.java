package ru.pronin.candlekafkaproducer.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class ExchangeInfo {
    private String timezone;
    private long serverTime;
    private List<Symbol> symbols;
}
