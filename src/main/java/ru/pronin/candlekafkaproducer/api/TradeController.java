package ru.pronin.candlekafkaproducer.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.pronin.candlekafkaproducer.broker.service.CandleProvider;
import ru.pronin.candlekafkaproducer.broker.tinkoff.TinkoffImpl;
import ru.pronin.candlekafkaproducer.dto.Candle;
import ru.pronin.candlekafkaproducer.dto.Order;
import ru.pronin.candlekafkaproducer.dto.request.CandleRequest;
import ru.tinkoff.piapi.contract.v1.PortfolioResponse;
import ru.tinkoff.piapi.contract.v1.PositionsResponse;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/trade")
public class TradeController {

    private final CandleProvider provider;
    private final TinkoffImpl broker;

    @PostMapping("/candles")
    public ResponseEntity<List<Candle>> getCandles(@RequestBody CandleRequest request) {
        List<Candle> result = provider.getCandles(request.getShare().getFigi(), request.getCount(), request.getCandleTime().getInterval().getTinkoffInterval());
        log.info("Запрос: {}, Ответ: {}", request, result);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/buy/market")
    public ResponseEntity<Order> marketBuy(@RequestParam String figi, @RequestParam double count) {
        return ResponseEntity.ok(broker.createBuyMarketOrder(figi, count));
    }

    @GetMapping("/buy/limit")
    public ResponseEntity<Order> limitBuy(@RequestParam String figi, @RequestParam double count, @RequestParam double price) {
        return ResponseEntity.ok(broker.createBuyLimitOrder(figi, count, price));
    }

    @GetMapping("/sell/market")
    public ResponseEntity<Order> marketSell(@RequestParam String figi, @RequestParam double count) {
        return ResponseEntity.ok(broker.createSellMarketOrder(figi, count));
    }

    @GetMapping("/sell/limit")
    public ResponseEntity<Order> marketSell(@RequestParam String figi, @RequestParam double count, @RequestParam double price) {
        return ResponseEntity.ok(broker.createSellLimitOrder(figi, count, price));
    }

    @GetMapping("/order")
    public ResponseEntity<Order> getOrder(@RequestParam String figi, @RequestParam String id) {
        return ResponseEntity.ok(broker.getOrder(figi, id));
    }

    @GetMapping("/cancel")
    public ResponseEntity<Void> cancelOrder(@RequestParam String figi, @RequestParam String id) {
        broker.cancelOrder(figi, id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/portfolio")
    public ResponseEntity<PortfolioResponse> getPortfolio() {
        return ResponseEntity.ok(broker.getPortfolio());
    }

    @GetMapping("/positions")
    public ResponseEntity<PositionsResponse> getPositions() {
        return ResponseEntity.ok(broker.getPositions());
    }
}
