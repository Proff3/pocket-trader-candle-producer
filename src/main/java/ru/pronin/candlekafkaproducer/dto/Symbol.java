package ru.pronin.candlekafkaproducer.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class Symbol {
    private String symbol;
    private String status;
    private String baseAsset;
    private String quoteAsset;
    private boolean isSpotTradingAllowed;
    private boolean isMarginTradingAllowed;
    private List<String> permissions;
}
