FROM alpine/java:21-jre

WORKDIR /app

# Копирование JAR-файла в контейнер
COPY target/*.jar kafka-producer.jar

# Команда для запуска JAR-файла
CMD ["java", "-jar", "kafka-producer.jar"]