package ru.pronin.candlekafkaproducer.broker.bybit;

import com.bybit.api.client.config.BybitApiConfig;
import com.bybit.api.client.domain.market.MarketInterval;
import com.bybit.api.client.service.BybitApiClientFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import ru.pronin.candlekafkaproducer.broker.BrokerAPI;
import ru.pronin.candlekafkaproducer.broker.BrokerType;
import ru.pronin.candlekafkaproducer.dto.Candle;
import ru.pronin.candlekafkaproducer.dto.Instrument;
import ru.pronin.candlekafkaproducer.dto.Operation;
import ru.pronin.candlekafkaproducer.dto.Order;
import ru.pronin.candlekafkaproducer.enums.CandleInterval;

import java.util.List;

public class BybitBroker implements BrokerAPI {

    @Value("${bybit.api.key}")
    private String apiKey;
    @Value("${bybit.api.secret}")
    private String apiSecret;

    private BybitApiClientFactory factory;

    @PostConstruct
    public void init() {
        factory = BybitApiClientFactory.newInstance(apiKey, apiSecret, BybitApiConfig.MAINNET_DOMAIN, true);
    }

    @Override
    public List<Candle> getRequiredNumberOfCandles(int requiredNumberOfCandles, String source, CandleInterval interval) throws JsonProcessingException {
        factory.newSpotMarginRestClient().get
        MarketInterval.FIVE_MINUTES
        return List.of();
    }

    @Override
    public Order createBuyMarketOrder(String source, Double quantity) {
        return null;
    }

    @Override
    public Order createSellMarketOrder(String source, Double quantity) {
        return null;
    }

    @Override
    public Order createBuyLimitOrder(String source, Double quantity, Double price) {
        return null;
    }

    @Override
    public Order createSellLimitOrder(String source, Double quantity, Double price) {
        return null;
    }

    @Override
    public Order marketCloseExistingOrder(String source, String id) {
        return null;
    }

    @Override
    public Order getOrder(String source, String orderId) {
        return null;
    }

    @Override
    public void cancelOrder(String source, String orderId) {

    }

    @Override
    public List<Operation> getAllOperations() {
        return List.of();
    }

    @Override
    public List<Instrument> getAllInstruments() {
        return List.of();
    }

    @Override
    public BrokerType getType() {
        return BrokerType.BYBIT;
    }
}
