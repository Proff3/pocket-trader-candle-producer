package ru.pronin.candlekafkaproducer.api;

import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.pronin.candlekafkaproducer.broker.stream.CandleStreamService;
import ru.pronin.candlekafkaproducer.dto.request.CandleTime;
import ru.pronin.candlekafkaproducer.enums.Share;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class InitTinkoffController {

    private final CandleStreamService streamService;

    @GetMapping("/create")
    public ResponseEntity<Void> createStream(@NotBlank @RequestParam String figi,
                                             @NotBlank @RequestParam CandleTime candleTime) {
        streamService.createProducer(Share.getByFigi(figi), candleTime);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/delete")
    public ResponseEntity<Void> deleteStream(@NotBlank @RequestParam String figi,
                                             @NotBlank @RequestParam CandleTime candleTime) {
        streamService.deleteProducer(Share.getByFigi(figi), candleTime);
        return ResponseEntity.ok().build();
    }
}
