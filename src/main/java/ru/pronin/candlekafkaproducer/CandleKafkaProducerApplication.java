package ru.pronin.candlekafkaproducer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import ru.pronin.candlekafkaproducer.broker.stream.CandleStreamService;
import ru.pronin.candlekafkaproducer.enums.Share;
import ru.pronin.candlekafkaproducer.enums.CandleInterval;

@Slf4j
@SpringBootApplication
public class CandleKafkaProducerApplication {

	public static void main(String[] args) {
		SpringApplication.run(CandleKafkaProducerApplication.class, args);
	}

}
