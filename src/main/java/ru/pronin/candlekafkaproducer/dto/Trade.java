package ru.pronin.candlekafkaproducer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class Trade {
    private String id;
    private LocalDateTime date;
    private double price;
    private long quantity;
}
