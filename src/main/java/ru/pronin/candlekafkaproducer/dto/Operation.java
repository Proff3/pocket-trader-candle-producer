package ru.pronin.candlekafkaproducer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class Operation {
    private String figi;
    private LocalDateTime Date;
    private double price;
    private long quantity;
    private String type;
    private String state;
    private List<Trade> trades;
}
