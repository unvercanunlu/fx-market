# fx-market

-- Requirements

1) Java 17
2) Docker

-- How to run:

1) docker-compose up -> run Kafka
2) java -jar fx-market-{version}.jar -> run app

-- Configuration

1) To send csv: (Kafka)
   - Host: localhost
   - Port: 9092
   - Topic: prices
   - group-id: fx-market
2) REST API:
   - Host: localhost
   - Port: 3535

-- Endpoint:

- /api/v1/prices/{currencyCode A}/{currencyCode B} -> the latest buy and sel rates from Currency A to Currency B
