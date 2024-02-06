package ru.pronin.candlekafkaproducer.broker.service;

import ru.pronin.candlekafkaproducer.broker.tinkoff.utils.TinkoffUtils;
import ru.pronin.candlekafkaproducer.dto.Order;
import ru.pronin.candlekafkaproducer.dto.OrderDirection;
import ru.pronin.candlekafkaproducer.dto.State;
import ru.tinkoff.piapi.contract.v1.OrderExecutionReportStatus;
import ru.tinkoff.piapi.contract.v1.OrderState;
import ru.tinkoff.piapi.contract.v1.PostOrderResponse;

import static ru.tinkoff.piapi.contract.v1.OrderDirection.ORDER_DIRECTION_BUY;

public class OrderProvider {

    public static Order provide(PostOrderResponse orderResponse, OrderDirection direction) {
        return Order.builder()
                .id(orderResponse.getOrderId())
                .source(orderResponse.getFigi())
                .quantity(orderResponse.getLotsExecuted())
                .commission(TinkoffUtils.fromMoneyValueToDouble(orderResponse.getExecutedCommission()))
                .state(getState(orderResponse.getExecutionReportStatus()))
                .averagePrice(TinkoffUtils.fromMoneyValueToDouble(orderResponse.getExecutedOrderPrice()))
                .direction(direction)
                .build();
    }

//    public static Order provide(BinanceOrder binanceOrder, String source, OrderDirection direction) {
//        return Order.builder()
//                .id(binanceOrder.getClientOrderId())
//                .source(source)
//                .quantity(getBinanceQuantity(binanceOrder.getFills()))
//                .commission(getBinanceCommission(binanceOrder.getFills()))
//                .state(binanceOrder.getStatus().getState())
//                .averagePrice(getBinancePrice(binanceOrder.getFills()))
//                .direction(direction)
//                .build();
//    }
//
//    public static Order provide(BinanceCanceledOrder binanceOrder, String source, OrderDirection direction) {
//        return Order.builder()
//                .id(binanceOrder.getClientOrderId())
//                .source(source)
//                .quantity(0)
//                .commission(0)
//                .state(binanceOrder.getStatus().getState())
//                .averagePrice(0)
//                .direction(direction)
//                .build();
//    }

    public static Order provide(OrderState orderState) {
        return Order.builder()
                .id(orderState.getOrderId())
                .source(orderState.getFigi())
                .quantity(orderState.getLotsExecuted())
                .commission(TinkoffUtils.fromMoneyValueToDouble(orderState.getExecutedCommission()))
                .state(getState(orderState.getExecutionReportStatus()))
                .averagePrice(TinkoffUtils.fromMoneyValueToDouble(orderState.getAveragePositionPrice()))
                .direction(getDirection(orderState.getDirection()))
                .build();
    }


//    private static double getBinanceQuantity(List<Fill> fills) {
//        return fills.stream().mapToDouble(Fill::getQty).sum();
//    }
//
//    private static double getBinanceCommission(List<Fill> fills) {
//        return fills.stream().mapToDouble(Fill::getQty).sum();
//    }
//
//    private static double getBinancePrice(List<Fill> fills) {
//        return fills.stream().collect(Collectors.averagingDouble(Fill::getPrice));
//    }

    private static State getState(OrderExecutionReportStatus status) {
        switch (status) {
            case EXECUTION_REPORT_STATUS_NEW -> {
                return State.NEW;
            }
            case EXECUTION_REPORT_STATUS_FILL -> {
                return State.FILLED;
            }
            case EXECUTION_REPORT_STATUS_PARTIALLYFILL -> {
                return State.PARTIALLY_FILLED;
            }
            case EXECUTION_REPORT_STATUS_CANCELLED -> {
                return State.CANCELLED;
            }
            default -> {
                return State.UNSPECIFIED;
            }
        }
    }

    private static OrderDirection getDirection(ru.tinkoff.piapi.contract.v1.OrderDirection od) {
        if (ORDER_DIRECTION_BUY.equals(od)) {
            return OrderDirection.BUY;
        }
        return OrderDirection.SELL;
    }
}
