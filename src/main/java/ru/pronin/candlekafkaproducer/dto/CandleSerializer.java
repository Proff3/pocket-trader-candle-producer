package ru.pronin.candlekafkaproducer.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.Serializer;

public class CandleSerializer implements Serializer<Candle> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public byte[] serialize(String topic, Candle data) {
        return new byte[0];
    }

    @Override
    public byte[] serialize(String topic, Headers headers, Candle data) {
        try {
            return objectMapper.writeValueAsBytes(data);
        } catch (JsonProcessingException e) {
            throw new SerializationException("Error when serializing to JSON", e);
        }
    }

    @Override
    public void close() {
        Serializer.super.close();
    }
}
