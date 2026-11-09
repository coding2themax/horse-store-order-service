# horse-store-order-service

## Overview

The `horse-store-order-service` is a backend service designed to manage orders for a horse store. It handles order creation, updates, and retrieval, ensuring a seamless experience for both customers and administrators.

## Features

- **Order Management**: Create, update, and delete orders.
- **Inventory Integration**: Check horse availability before placing orders.
- **Customer Notifications**: Notify customers about order status updates.
- **Secure Payments**: Integrate with payment gateways for secure transactions.

## Installation

1. Clone the repository:

```bash
git clone https://github.com/your-repo/horse-store-order-service.git
```

2. Navigate to the project directory:

```bash
cd horse-store-order-service
```

3. Install dependencies:

```bash
mvn install
```

4. Run the tests:

```bash
mvn verify
```

   This fails if any package is below 90% code coverage. `FeedPurchaseStoreTest`
   starts Postgres with Testcontainers, so Docker must be running. CI runs the
   same command on every pull request.

## Usage

1. Start the service:

```bash
java -jar target/horse-store-order-service.jar
```

2. Access the API at `http://localhost:13000`.

## API Endpoints

- `POST /orders`: Create a new order.
- `GET /orders/:id`: Retrieve order details by ID.
- `PUT /orders/:id`: Update an existing order.
- `DELETE /orders/:id`: Cancel an order.
- `POST /feed-purchases`: Buy feed. Starts the feed purchase saga and returns 202.
- `GET /feed-purchases/{sagaId}`: The finished feed purchase, or 404 while the saga is still running.

## Feed Purchase Saga

A user buys horse feed through a saga. The steps talk over RabbitMQ, and the
purchase is written to Postgres only once the saga has finished.

```
POST /feed-purchases
        │
        ▼
 inventory.reserve ──► Horse-Inventory-Service
        │                  ├─ rejected ─────────────────────────► save CANCELLED
        ▼ reserved (with price)
 payment.charge ──► payment step (this service)
        │                  └─ failed ──► inventory.release ──────► save CANCELLED
        ▼ completed
 inventory.commit ──► Horse-Inventory-Service
        │
        ▼ committed
 save COMPLETED
```

- **Orchestrator:** `saga/FeedPurchaseSagaOrchestrator` sends each command and
  reacts to each reply.
- **No in-flight rows:** the whole saga state travels inside the message
  (`saga/FeedPurchaseMessage`). Postgres gets one row per purchase in
  `feed_purchase`, written when the saga ends as `COMPLETED` or `CANCELLED`.
- **Compensation:** if payment fails, the reserved stock is released before the
  purchase is saved as `CANCELLED`.
- **Retries:** every step is safe to run twice. A message that still fails after
  3 attempts goes to the `feed-purchase.dead-letter` queue. This includes the
  final save if Postgres is down, so the outcome can be replayed later.
- **Payment:** `payment/PaymentParticipant` is a stand-in for a real payment
  gateway. It declines tokens starting with `tok_declined` and totals above
  `payment.max-amount` (default 5000.00). It only talks over RabbitMQ, so it
  can move to its own service later.

### RabbitMQ names

| Name | Kind | Used for |
| --- | --- | --- |
| `feed.purchase.saga` | topic exchange | all saga messages |
| `feed-purchase.inventory.commands` | queue (`inventory.*`) | read by Horse-Inventory-Service |
| `feed-purchase.payment.commands` | queue (`payment.*`) | read by the payment step |
| `feed-purchase.saga.replies` | queue (`saga.#`) | read by the orchestrator |
| `feed-purchase.dead-letter` | queue | messages that kept failing |

### Try it

```bash
# Postgres and RabbitMQ
DATABASE_USERNAME=postgres DATABASE_PASSWORD=postgres docker compose up -d db rabbitmq

# This service (port 13000) and Horse-Inventory-Service (port 8080)
./mvnw spring-boot:run
(cd ../Horse-Inventory-Service && ./mvnw spring-boot:run)

# Buy feed: returns 202 with a sagaId
curl -X POST localhost:13000/feed-purchases -H 'Content-Type: application/json' \
  -d '{"customerId":"cust-1","feedId":"alfalfa-hay","quantity":5,"paymentToken":"tok_visa"}'

# Returns the purchase once the saga has finished, 404 until then
curl localhost:13000/feed-purchases/<sagaId>
```

Feed ids: `alfalfa-hay`, `premium-pellets`, `senior-feed`, `performance-mix`,
`maintenance-cubes`. See current stock at `GET localhost:8080/feed-stock`.

| Setting | Default |
| --- | --- |
| `DATABASE_HOST` / `DATABASE_PORT` / `DATABASE_NAME` | `localhost` / `5432` / `horse-order-service` |
| `DATABASE_USERNAME` / `DATABASE_PASSWORD` | `postgres` / `postgres` |
| `RABBITMQ_HOST` / `RABBITMQ_PORT` | `localhost` / `5672` |
| `RABBITMQ_USERNAME` / `RABBITMQ_PASSWORD` | `guest` / `guest` |

## Contributing

Contributions are welcome! Please fork the repository and submit a pull request.

## License

This project is licensed under the Apache License 2.0. See Lincense tab.
