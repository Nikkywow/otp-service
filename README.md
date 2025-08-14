# OTP-сервис

## Описание

Сервис для генерации и проверки одноразовых паролей (OTP) с возможностью отправки через различные каналы связи. Решение предназначено для реализации двухфакторной аутентификации, подтверждения операций и других сценариев верификации пользователей.

## Основные возможности

- Генерация OTP-кодов с настройкой:
  - Длины кода
  - Времени жизни
- Поддержка каналов доставки:
  - Email (через SMTP)
  - SMS (через SMPP-шлюз)
  - Telegram-уведомления
  - Запись в файл (для разработки)
- REST API для интеграции
- Административный интерфейс управления
- Мониторинг работы через Spring Boot Actuator

## Технологии

- **Бэкенд**: Java 11, Spring Boot 2.7
- **База данных**: PostgreSQL
- **Миграции БД**: Liquibase
- **Аутентификация**: JWT-токены
- **Ограничение запросов**: Bucket4j
- **Мониторинг**: Micrometer + Prometheus
- **Отправка сообщений**: SMTP, SMPP, Telegram Bot API

## Быстрый старт

### Требования

- Java 11 или новее
- PostgreSQL 12+
- Maven 3.6+

### Установка

1. Настройте базу данных:
```sql
CREATE DATABASE otp_service;
CREATE USER otp_user WITH PASSWORD 'password';
GRANT ALL PRIVILEGES ON DATABASE otp_service TO otp_user;
```

2. Настройте конфигурацию:
```bash
cp src/main/resources/application.example.properties src/main/resources/application.properties
```

3. Обновите настройки в файле application.properties:
```properties
# Настройки БД
spring.datasource.url=jdbc:postgresql://localhost:5432/otp_service
spring.datasource.username=otp_user
spring.datasource.password=password

# Настройки email
email.smtp.host=smtp.yourdomain.com
email.smtp.port=587
email.username=your@email.com
email.password=email-password

# Настройки SMS
smpp.host=sms-gateway-host
smpp.port=2775
smpp.system_id=your-smpp-login
smpp.password=smpp-password

# Настройки Telegram
telegram.bot.token=your-bot-token
telegram.chat.id=target-chat-id
```

4. Соберите и запустите проект:
```bash
mvn clean install
java -jar target/otp-service-0.0.1-SNAPSHOT.jar
```

## API документация

### Аутентификация

**Регистрация пользователя**
```
POST /api/auth/register
Content-Type: application/json

{
  "username": "newuser",
  "password": "password123",
  "role": "USER"
}
```

**Вход в систему**
```
POST /api/auth/login
Content-Type: application/json

{
  "username": "existinguser",
  "password": "userpassword"
}
```

### Работа с OTP

**Генерация OTP**
```
POST /api/otp/generate
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json

{
  "channel": "EMAIL|SMS|TELEGRAM|FILE",
  "destination": "target@email.com или номер телефона",
  "operationId": "уникальный-id-операции"
}
```

**Проверка OTP**
```
POST /api/otp/validate
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json

{
  "code": "полученный-код",
  "operationId": "id-операции"
}
```

## Администрирование

Административные endpoints доступны по пути `/api/admin` и требуют роли ADMIN.

## Мониторинг

Метрики для Prometheus доступны по адресу:
```
GET /actuator/prometheus
```

## Для разработчиков

### Добавление нового канала отправки

1. Создайте новый сервис в пакете `com.otp.service.notification`
2. Реализуйте логику отправки уведомлений
3. Зарегистрируйте сервис в `NotificationService`
4. Добавьте название канала в валидацию запросов OTP

### Работа с миграциями базы данных

Миграции Liquibase находятся в `src/main/resources/db/changelog`. Для создания новой миграции:

1. Добавьте новый changeset в db.changelog-master.yaml
2. Создайте файл миграции в формате YAML
