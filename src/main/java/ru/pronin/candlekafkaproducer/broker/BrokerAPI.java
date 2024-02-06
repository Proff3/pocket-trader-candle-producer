package ru.pronin.candlekafkaproducer.broker;

import com.fasterxml.jackson.core.JsonProcessingException;
import ru.pronin.candlekafkaproducer.dto.Candle;
import ru.pronin.candlekafkaproducer.dto.Instrument;
import ru.pronin.candlekafkaproducer.dto.Operation;
import ru.pronin.candlekafkaproducer.dto.Order;
import ru.pronin.candlekafkaproducer.enums.CandleInterval;

import java.util.List;

public interface BrokerAPI {
    List<Candle> getRequiredNumberOfCandles(int requiredNumberOfCandles, String source, CandleInterval interval)  throws JsonProcessingException;
    Order createBuyMarketOrder(String source, Double quantity);
    Order createSellMarketOrder(String source, Double quantity);
    Order createBuyLimitOrder(String source, Double quantity, Double price);
    Order createSellLimitOrder(String source, Double quantity, Double price);
    Order marketCloseExistingOrder(String source, String id);
    Order getOrder(String source, String orderId);
    void cancelOrder(String source, String orderId);
    List<Operation> getAllOperations();
    List<Instrument> getAllInstruments();
    BrokerType getType();
}
