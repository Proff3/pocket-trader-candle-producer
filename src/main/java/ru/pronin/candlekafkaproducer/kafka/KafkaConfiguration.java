package ru.pronin.candlekafkaproducer.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import ru.pronin.candlekafkaproducer.dto.CandleDto;

@EnableKafka
@Configuration
@RequiredArgsConstructor
public class KafkaConfiguration {

    private final KafkaProperties kafkaProperties;

    @Bean
    public ProducerFactory<String, CandleDto> candleProducerFactory() {
        return new DefaultKafkaProducerFactory<>(kafkaProperties.buildProducerProperties());
    }

    @Bean
    public KafkaTemplate<String, CandleDto> candleKafkaTemplate() {
        return new KafkaTemplate<>(candleProducerFactory());
    }
}
