package ru.pronin.candlekafkaproducer.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.pronin.candlekafkaproducer.database.UserRepository;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class Test {

    private final UserRepository userRepository;

    @GetMapping("/test")
    public ResponseEntity<String> test(@RequestHeader(name = AUTHORIZATION) String auth) {
        System.out.println(auth);
        return ResponseEntity.ok(userRepository.findAll().get(0).getEmail());
    }
}
