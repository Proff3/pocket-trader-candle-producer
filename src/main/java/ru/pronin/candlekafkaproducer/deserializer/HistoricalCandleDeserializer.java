package ru.pronin.candlekafkaproducer.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.node.ArrayNode;
import ru.pronin.candlekafkaproducer.dto.HistoricCandle;

import java.io.IOException;
import java.math.BigDecimal;

public class HistoricalCandleDeserializer extends JsonDeserializer<HistoricCandle> {
    @Override
    public HistoricCandle deserialize(JsonParser p, DeserializationContext ctx) throws IOException {
        ObjectCodec oc = p.getCodec();
        ArrayNode node = oc.readTree(p);
        return new HistoricCandle(
                node.get(0).asLong(),
                new BigDecimal(node.get(1).asText()),
                new BigDecimal(node.get(2).asText()),
                new BigDecimal(node.get(3).asText()),
                new BigDecimal(node.get(4).asText()),
                new BigDecimal(node.get(5).asText()),
                node.get(6).asLong(),
                new BigDecimal(node.get(7).asText()),
                node.get(8).asInt(),
                new BigDecimal(node.get(9).asText()),
                new BigDecimal(node.get(10).asText()));
    }
}
