package ru.pronin.candlekafkaproducer.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@ToString
@Builder
public class Order {
    private String id;
    private String source;
    private double quantity;
    private double averagePrice;
    private State state;
    private double commission;
    @JsonIgnore
    private OrderDirection direction;
    private LocalDateTime time;
}
