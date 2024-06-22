package ru.pronin.candlekafkaproducer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CandleDto {

    private String figi;
    private Candle candle;
}
