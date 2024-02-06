package ru.pronin.candlekafkaproducer.broker.tinkoff;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.pronin.candlekafkaproducer.broker.BrokerAPI;
import ru.pronin.candlekafkaproducer.broker.BrokerType;
import ru.pronin.candlekafkaproducer.dto.Order;
import ru.tinkoff.piapi.contract.v1.*;
import ru.tinkoff.piapi.core.InstrumentsService;
import ru.tinkoff.piapi.core.InvestApi;
import ru.tinkoff.piapi.core.MarketDataService;
import ru.tinkoff.piapi.core.SandboxService;
import ru.tinkoff.piapi.core.stream.MarketDataStreamService;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static ru.pronin.candlekafkaproducer.broker.service.OrderProvider.provide;
import static ru.pronin.candlekafkaproducer.broker.tinkoff.utils.TinkoffUtils.*;
import static ru.tinkoff.piapi.contract.v1.CandleInterval.*;

@Service
@Slf4j
public class TinkoffImpl implements BrokerAPI {

    @Value("${tinkoff.api.token.prod}")
    private String token;
    private String accountId;
    private SandboxService sandboxService;
    private InstrumentsService instrumentsService;
    private MarketDataService marketDataService;
    private MarketDataStreamService marketDataStreamService;

    @PostConstruct
    public void init() {
        InvestApi investApi = InvestApi.create(token);
        instrumentsService = investApi.getInstrumentsService();
        marketDataService = investApi.getMarketDataService();
        sandboxService = investApi.getSandboxService();
        marketDataStreamService = investApi.getMarketDataStreamService();
        accountId = sandboxService.openAccountSync();
        System.out.println("Account id " + accountId);
        sandboxService.payInSync(accountId, fromDoubleToMoneyValue(99_000, "USD"));
        sandboxService.payInSync(accountId, fromDoubleToMoneyValue(5_000_000, "RUB"));
    }

    @Override
    public List<ru.pronin.candlekafkaproducer.dto.Candle> getRequiredNumberOfCandles(int requiredNumberOfCandles, String source, ru.pronin.candlekafkaproducer.enums.CandleInterval interval) {
        long minutes = ru.pronin.candlekafkaproducer.enums.CandleInterval.getMinutesRange(interval);
        Instant from = LocalDateTime.now().minusMinutes(requiredNumberOfCandles * minutes).toInstant(ZoneOffset.UTC);
        Instant to = LocalDateTime.now().toInstant(ZoneOffset.UTC);
        List<HistoricCandle> candles = new ArrayList<>();
        while (requiredNumberOfCandles > candles.size()) {
            candles.addAll(0, marketDataService.getCandlesSync(source, from, to, getInterval(interval)));
            to = from;
            from = LocalDateTime.ofInstant(from, ZoneOffset.UTC).minusMinutes(requiredNumberOfCandles * minutes).toInstant(ZoneOffset.UTC);
        }
        return candles.stream()
                .skip(candles.size() - requiredNumberOfCandles)
                .map(ru.pronin.candlekafkaproducer.dto.Candle::new)
                .map(candle -> candle.withCloseTime(candle.getOpenTime().plusMinutes(ru.pronin.candlekafkaproducer.enums.CandleInterval.getMinutesRange(interval))))
                .collect(Collectors.toList());
    }

    @Override
    public Order createBuyMarketOrder(String source, Double quantity) {
        String id = UUID.randomUUID().toString();
        PostOrderResponse order = sandboxService.postOrderSync(
                source,
                quantity.longValue(),
                Quotation.newBuilder().build(),
                OrderDirection.ORDER_DIRECTION_BUY,
                accountId,
                OrderType.ORDER_TYPE_MARKET,
                id);
        Order orderToReturn = provide(order, ru.pronin.candlekafkaproducer.dto.OrderDirection.BUY);
        orderToReturn.setTime(LocalDateTime.now());
        return orderToReturn;
    }

    @Override
    public Order createSellMarketOrder(String source, Double quantity) {
        String id = UUID.randomUUID().toString();
        PostOrderResponse order = sandboxService.postOrderSync(
                source,
                quantity.longValue(),
                Quotation.newBuilder().build(),
                OrderDirection.ORDER_DIRECTION_SELL,
                accountId,
                OrderType.ORDER_TYPE_MARKET,
                id);
        Order orderToReturn = provide(order, ru.pronin.candlekafkaproducer.dto.OrderDirection.SELL);
        orderToReturn.setTime(LocalDateTime.now());
        return orderToReturn;
    }

    @Override
    public Order createBuyLimitOrder(String source, Double quantity, Double price) {
        String id = UUID.randomUUID().toString();
        PostOrderResponse order = sandboxService.postOrderSync(
                source,
                quantity.longValue(),
                fromDoubleToQuotation(price),
                OrderDirection.ORDER_DIRECTION_BUY,
                accountId,
                OrderType.ORDER_TYPE_LIMIT,
                id);
        Order orderToReturn = provide(order, ru.pronin.candlekafkaproducer.dto.OrderDirection.BUY);
        orderToReturn.setTime(LocalDateTime.now());
        return orderToReturn;
    }

    @Override
    public Order createSellLimitOrder(String source, Double quantity, Double price) {
        String id = UUID.randomUUID().toString();
        PostOrderResponse order = sandboxService.postOrderSync(
                source,
                quantity.longValue(),
                fromDoubleToQuotation(price),
                OrderDirection.ORDER_DIRECTION_SELL,
                accountId,
                OrderType.ORDER_TYPE_LIMIT,
                id);
        Order orderToReturn = provide(order, ru.pronin.candlekafkaproducer.dto.OrderDirection.SELL);
        orderToReturn.setTime(LocalDateTime.now());
        return orderToReturn;
    }

    @Override
    public Order marketCloseExistingOrder(String source, String id) {
        OrderState order = getOrderState(source, id);
        sandboxService.cancelOrder(accountId, id);
        long requiredToClose = order.getLotsRequested() - order.getLotsExecuted();
        OrderDirection direction = order.getDirection();
        if (OrderDirection.ORDER_DIRECTION_BUY.equals(direction)) {
            return createBuyMarketOrder(source, (double) requiredToClose);
        } else if (OrderDirection.ORDER_DIRECTION_SELL.equals(direction)) {
            return createSellMarketOrder(source, (double) requiredToClose);
        }
        throw new RuntimeException("Unknown direction " + direction.name());
    }

    @Override
    public Order getOrder(String source, String orderId) {
        Order orderToReturn = provide(getOrderState(source, orderId));
        orderToReturn.setTime(LocalDateTime.now());
        return orderToReturn;
    }

    private OrderState getOrderState(String source, String orderId) {
        OrderState orderState = null;
        try {
            orderState = sandboxService.getOrderStateSync(accountId, orderId);
        } catch (Exception ex) {
            log.error("Ошибка при запросе ордера {} с figi {}", orderId, source, ex);
        }
        return orderState;
    }

    @Override
    public void cancelOrder(String source, String orderId) {
        sandboxService.cancelOrder(accountId, orderId);
    }

    @Override
    public List<ru.pronin.candlekafkaproducer.dto.Operation> getAllOperations() {
        return ru.pronin.candlekafkaproducer.enums.Share
                .get()
                .stream()
                .flatMap(share -> Arrays
                        .stream(OperationState.values())
                        .flatMap(state ->
                                sandboxService
                                        .getOperationsSync(
                                                accountId,
                                                LocalDateTime.now().minusYears(1L).toInstant(ZoneOffset.UTC),
                                                LocalDateTime.now().toInstant(ZoneOffset.UTC),
                                                state,
                                                share.getFigi())
                                        .stream()))
                .map(operation -> new ru.pronin.candlekafkaproducer.dto.Operation(
                        operation.getFigi(),
                        LocalDateTime.ofEpochSecond(operation.getDate().getSeconds(), 0, ZoneOffset.UTC),
                        fromMoneyValueToDouble(operation.getPrice()),
                        operation.getQuantity(),
                        operation.getType(),
                        operation.getState().name(),
                        operation
                                .getTradesList()
                                .stream()
                                .map(trade -> new ru.pronin.candlekafkaproducer.dto.Trade(
                                        trade.getTradeId(),
                                        LocalDateTime.ofEpochSecond(trade.getDateTime().getSeconds(), 0, ZoneOffset.UTC),
                                        fromMoneyValueToDouble(trade.getPrice()),
                                        trade.getQuantity()))
                                .collect(Collectors.toList())
                ))
                .collect(Collectors.toList());
    }

    @Override
    public List<ru.pronin.candlekafkaproducer.dto.Instrument> getAllInstruments() {
        return instrumentsService.getAllSharesSync()
                .stream()
                .map(share -> new ru.pronin.candlekafkaproducer.dto.Instrument(
                        share.getFigi(),
                        share.getName(),
                        share.getCurrency()
                ))
                .collect(Collectors.toList());
    }

    public InstrumentsService getInstrumentService() {
        return instrumentsService;
    }

    @Override
    public BrokerType getType() {
        return BrokerType.TINKOFF;
    }

    public MarketDataStreamService getMarketDataStreamService() {
        return marketDataStreamService;
    }

    public PositionsResponse getPositions() {
        return sandboxService.getPositionsSync(accountId);
    }

    public PortfolioResponse getPortfolio() {
        return sandboxService.getPortfolioSync(accountId);
    }

    public MarketDataService getMarketDataService() {
        return marketDataService;
    }

    private ru.tinkoff.piapi.contract.v1.CandleInterval getInterval(ru.pronin.candlekafkaproducer.enums.CandleInterval interval) {
        switch (interval) {
            case ONE_MINUTE: {
                return CANDLE_INTERVAL_1_MIN;
            }
            case FIVE_MINUTES: {
                return CANDLE_INTERVAL_5_MIN;
            }
            case FIFTEEN_MINUTES: {
                return CANDLE_INTERVAL_15_MIN;
            }
            case HOURLY: {
                return CANDLE_INTERVAL_HOUR;
            }
            case DAILY: {
                return CANDLE_INTERVAL_DAY;
            }
            default: {
                return CANDLE_INTERVAL_UNSPECIFIED;
            }
        }
    }
}
