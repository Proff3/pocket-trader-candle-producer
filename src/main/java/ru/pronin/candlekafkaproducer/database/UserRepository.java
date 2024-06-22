package ru.pronin.candlekafkaproducer.database;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;


public interface UserRepository extends JpaRepository<UserEntity, UUID> {
}
