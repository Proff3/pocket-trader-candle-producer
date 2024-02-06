package ru.pronin.candlekafkaproducer.dto.request;

import lombok.Data;
import ru.pronin.candlekafkaproducer.enums.Share;

import java.io.Serial;
import java.io.Serializable;

@Data
public class CandleRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 8682743109469213285L;

    private Share share;
    private int count;
    private CandleTime candleTime;
    private boolean isPrimeTime = true;
}
