# Lumen Coin API - Message Queue System

## Overview

The Lumen Coin API uses **RabbitMQ** for asynchronous processing of coin transfer transactions. This ensures that coin balance updates are handled reliably and decoupled from the HTTP request-response cycle.

### Why RabbitMQ?

- **Decoupling** - The teacher who sends coins doesn't wait for the student's balance to update.
- **Reliability** - Guaranteed delivery with dead-letter queues for failed messages.
- **Scalability** - Multiple consumers can process messages in parallel.
- **Resilience** - Messages are persisted; the system survives restarts.

---

## Architecture

### Message Flow

```
1. Teacher clicks "Send Coins"
        ↓
2. CoinTransferController receives POST /coin-transfers
        ↓
3. CoinTransferServiceImpl validates & deducts teacher balance (synchronous)
        ↓
4. CoinTransfer entity saved to database
        ↓
5. CoinTransferMessage published to RabbitMQ exchange
        ↓
6. CoinTransferConsumer picks up message from queue (asynchronous)
        ↓
7. Student balance credited
        ↓
8. If error: message moves to Dead Letter Queue (DLQ)
```

### Components

| Component                   | Purpose                                                       |
| --------------------------- | ------------------------------------------------------------- |
| **RabbitMQConfig**          | Declares queues, exchanges, bindings, converters              |
| **CoinTransferProducer**    | Publishes CoinTransferMessage to RabbitMQ exchange            |
| **CoinTransferConsumer**    | Listens to queue, receives messages, credits student balance  |
| **CoinTransferMessage**     | DTO: `recipientId` (UUID), `amount` (int)                     |
| **Dead Letter Queue (DLQ)** | Captures messages that failed processing (manual retry point) |

---

## Configuration

### Docker Compose Setup

The RabbitMQ service is defined in `docker-compose.yml`:

```yaml
rabbitmq:
    image: rabbitmq:4.0-management
    container_name: lumen-rabbitmq
    ports:
        - '5672:5672' # AMQP protocol port
        - '15672:15672' # Management UI port
    environment:
        RABBITMQ_DEFAULT_USER: guest
        RABBITMQ_DEFAULT_PASS: guest
        RABBITMQ_DEFAULT_VHOST: /
    volumes:
        - rabbitmq-data:/var/lib/rabbitmq
    networks:
        - lumen-network

volumes:
    rabbitmq-data:
```

The **Management UI** is accessible at `http://localhost:15672` (login: `guest` / `guest`).

### Spring Boot Configuration

In `application.properties`:

```properties
spring.rabbitmq.host=localhost
spring.rabbitmq.port=5672
spring.rabbitmq.username=guest
spring.rabbitmq.password=guest
spring.rabbitmq.virtual-host=/
```

Environment variables can override these defaults:

```bash
export SPRING_RABBITMQ_HOST=your-rabbitmq-host
export SPRING_RABBITMQ_PORT=5672
export SPRING_RABBITMQ_USERNAME=guest
export SPRING_RABBITMQ_PASSWORD=guest
```

---

## Queue & Exchange Setup

### RabbitMQConfig

The `RabbitMQConfig` class (in `config/` package) declares:

| Name                     | Type           | Description                                   |
| ------------------------ | -------------- | --------------------------------------------- |
| `coin.transfer`          | Queue          | Main queue for coin transfer messages         |
| `coin.transfer.dlq`      | Queue          | Dead Letter Queue for failed messages         |
| `coin.transfer.exchange` | DirectExchange | Routes messages to the main queue             |
| `coin.transfer.binding`  | Binding        | Binds exchange to main queue with routing key |

### Message Converter

**Jackson2JsonMessageConverter** is configured to serialize/deserialize `CoinTransferMessage` objects as JSON.

```
Message Format (JSON):
{
  "recipientId": "550e8400-e29b-41d4-a716-446655440000",
  "amount": 50
}
```

---

## Running Locally

### 1. Start RabbitMQ

```bash
docker compose up -d rabbitmq
```

Verify it's running:

```bash
docker logs -f lumen-rabbitmq
```

### 2. Start the API

```bash
./mvnw spring-boot:run
```

### 3. Open Management UI

Visit `http://localhost:15672` to inspect queues, exchanges, and messages in real-time.

---

## Testing the Message Flow

### Send a Coin Transfer (Manual)

```bash
# Login as teacher and get JWT token
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"teacher@example.com","password":"password123"}'

# Extract JWT from response cookie or body

# Send coins
curl -X POST http://localhost:8080/coin-transfers \
  -H "Authorization: Bearer <JWT>" \
  -H "Content-Type: application/json" \
  -d '{
    "studentId":"550e8400-e29b-41d4-a716-446655440000",
    "amount":50,
    "message":"Great presentation!"
  }'
```

### Monitor Messages in RabbitMQ UI

1. Go to `http://localhost:15672`
2. Click **Queues and Streams**
3. Select `coin.transfer` queue
4. Click **Get Messages** to peek at pending messages
5. Click on a message to view its details (JSON body)

### Check Dead Letter Queue (DLQ)

If a message fails processing (e.g., student not found), it moves to `coin.transfer.dlq`:

1. In Management UI, select `coin.transfer.dlq`
2. Click **Get Messages** to see failed messages
3. Fix the underlying issue (e.g., create the student)
4. Manually republish or investigate the error

---

## Message Processing Details

### Producer: CoinTransferProducer

```java
public void send(CoinTransferMessage message) {
    rabbitTemplate.convertAndSend(
        COIN_TRANSFER_EXCHANGE,
        COIN_TRANSFER_ROUTING_KEY,
        message
    );
}
```

- Converts the Java object to JSON
- Sends to the configured exchange with routing key
- Message is routed to `coin.transfer` queue

### Consumer: CoinTransferConsumer

```java
@RabbitListener(queues = COIN_TRANSFER_QUEUE)
@Transactional
public void process(CoinTransferMessage message) {
    Student student = studentRepository.findById(message.recipientId())
        .orElseThrow(() -> new UserNotFoundException("Student not found"));
    student.setBalance(student.getBalance() + message.amount());
    studentRepository.save(student);
}
```

- Annotated with `@RabbitListener` to automatically consume from queue
- Marked `@Transactional` to ensure database consistency
- On success: message is acknowledged and removed from queue
- On exception: message is nack'd and moves to DLQ (if configured)

---

## Error Handling

### Scenarios

| Scenario          | Behavior                                |
| ----------------- | --------------------------------------- |
| Student exists    | Message processed, balance updated      |
| Student not found | Exception thrown, message → DLQ         |
| Database error    | Transactional rollback, message → DLQ   |
| Consumer down     | Messages queue up; processed on restart |

### Retry Strategy

Messages in the DLQ are **not automatically retried**. To retry:

1. Investigate the error in Management UI
2. Fix the root cause (e.g., create missing student)
3. Use RabbitMQ Management UI to **purge** or **republish** messages

For production, consider adding a retry consumer that republishes failed messages after a delay.

---

## Semester Coin Top-Up Scheduler

### Scheduled Task

Teachers receive **1000 coins** on:

- **February 1st** (start of S1)
- **August 1st** (start of S2)

Configured in `SemesterCoinTopUpScheduler`:

```java
@Scheduled(cron = "0 0 0 1 2,8 *", zone = "America/Sao_Paulo")
public void topUpTeacherBalances() {
    // Adds 1000 coins to all teachers
}
```

- Cron: `0 0 0 1 2,8 *` = 00:00:00 on Feb 1st and Aug 1st
- Timezone: `America/Sao_Paulo` (GMT -3)
- No RabbitMQ involved; runs directly in the database

---

## Troubleshooting

### RabbitMQ not running?

```bash
docker compose logs rabbitmq
docker compose restart rabbitmq
```

### Messages stuck in queue?

1. Check for exceptions in Spring Boot logs: `grep -i error logs`
2. View message details in Management UI
3. If consumer is down, restart the API: `./mvnw spring-boot:run`

### Connection refused?

- Verify RabbitMQ container is running: `docker ps`
- Check ports: `docker port lumen-rabbitmq`
- Verify properties in `application.properties` match your setup

### Test with Empty Queue?

```bash
curl http://localhost:15672/api/queues/%2F/coin.transfer \
  -u guest:guest | jq '.messages'
```

Shows current message count in `coin.transfer` queue.

---

## Best Practices

1. **Always use `@Transactional`** on consumers to ensure database consistency.
2. **Monitor the DLQ** in production; set up alerts for messages moving there.
3. **Test failure scenarios** - what happens if the student is deleted while a message is in flight?
4. **Scale consumers** - if coin transfers are high-volume, run multiple consumer instances.
5. **Log significant events** - see `CoinTransferConsumer` for SLF4J logging pattern.

---

## Related Files

- `config/RabbitMQConfig.java` - Queue, exchange, binding declarations
- `coin_transfer/messaging/CoinTransferProducer.java` - Sends messages
- `coin_transfer/messaging/CoinTransferConsumer.java` - Consumes messages
- `coin_transfer/messaging/CoinTransferMessage.java` - Message DTO
- `coin_transfer/scheduler/SemesterCoinTopUpScheduler.java` - Scheduler for top-ups
- `docker-compose.yml` - RabbitMQ service definition
- `application.properties` - RabbitMQ connection properties
