FROM bitnami/java:17.0.6-10-debian-11-r4

WORKDIR /app

# Копирование JAR-файла в контейнер
COPY target/*.jar kafka-producer.jar

# Команда для запуска JAR-файла
CMD ["java", "-jar", "kafka-producer.jar"]