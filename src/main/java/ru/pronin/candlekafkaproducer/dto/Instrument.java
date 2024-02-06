package ru.pronin.candlekafkaproducer.dto;

import lombok.*;

@AllArgsConstructor
@ToString
@Builder
@Getter
@Setter
@NoArgsConstructor
public class Instrument {
    String figi;
    String name;
    String currency;
}
