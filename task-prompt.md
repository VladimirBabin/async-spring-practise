# Task: Implement Trade Calculations Dashboard API

## 1. Project Setup & Dependencies

- **Framework:** Spring Boot with Gradle.
- **Dependencies:**
    - `spring-boot-starter-web`
    - `spring-boot-starter-webflux` (for reactive components if needed for async operations)
    - `spring-boot-starter-data-jpa`
    - `postgresql` driver
    - `flyway-core` for database migrations.
    - `spring-boot-starter-test` (for testing)
    - `org.projectlombok:lombok` (optional, for boilerplate code reduction)
- **Configuration:**
    - Create `src/main/resources/application.yml`.
    - Configure the application port to `8099`.
    - Add datasource configuration for a PostgreSQL database.

## 2. Database Schema and Data

- **Migration:**
    - Use Flyway to manage database schema changes.
    - Create a migration script in `src/main/resources/db/migration/` named `V1__Create_trades_table.sql`.
    - The script should create a `trades` table with the following columns:
        - `id` (BIGINT, Primary Key, Generated)
        - `buying_account_id` (BIGINT, Not Null)
        - `vendor_account_id` (BIGINT, Not Null)
        - `product_name` (VARCHAR(255), Not Null)
        - `product_quantity` (INT, Not Null)
        - `product_buying_price` (DECIMAL(19, 2), Not Null)
        - `product_selling_price` (DECIMAL(19, 2), Not Null)
- **Seed Data:**
    - Create a directory `scripts` in the project root.
    - Inside `scripts`, create a file `trades-data.sql`.
    - Add SQL `INSERT` statements to populate the `trades` table with 10 sample records. Ensure `product_buying_price` is less than `product_selling_price` and `product_quantity` is positive.

## 3. Hexagonal Architecture Implementation

### 3.1. Domain (Core)

- **Models:**
    - `Trade`: Represents a trade with fields matching the `trades` table.
    - `Payment`: Represents a payment with fields: `id`, `tradeId`, `type` (Enum: `BUYER`, `VENDOR`), `amount` (BigDecimal), `currency` (String, default "USD"), `payment_date` (LocalDate).
- **DTO:**
    - `TradeCalculationDto`: A record or class to hold the calculated values:
        - `tradeId` (Long)
        - `grossProfit` (BigDecimal)
        - `costOfGoods` (BigDecimal)
        - `balance` (BigDecimal)
- **Ports (Interfaces):**
    - Create an `application/ports/in` package for inbound ports (use cases).
        - `TradeCalculationUseCase`: Defines the method to get trade calculations, e.g., `List<TradeCalculationDto> getTradeCalculations();`.
    - Create an `application/ports/out` package for outbound ports.
        - `GetTradesPort`: Defines a method to fetch all trades, e.g., `CompletableFuture<List<Trade>> getAllTrades();`.
        - `GetPaymentsPort`: Defines a method to fetch all tradePayments, e.g., `CompletableFuture<List<Payment>> getAllPayments();`.

### 3.2. Application (Use Case Implementation)

- Create a `service` or `usecase` package inside `application`.
- `TradeCalculationService` implementing `TradeCalculationUseCase`.
    - Inject `GetTradesPort` and `GetPaymentsPort`.
    - Implement the `getTradeCalculations` method:
        1.  Enable asynchronous execution by adding `@EnableAsync` to `AsyncSpringPracticeApplication`.
        2.  The service implementation itself should be annotated with `@Service`.
        3.  The `getTradeCalculations` method should call the port methods asynchronously.
        4.  Wait for both `CompletableFuture` results to complete.
        5.  Process the lists of trades and tradePayments to calculate the required DTOs:
            - `grossProfit` = `product_quantity * product_buying_price`
            - `costOfGoods` = `product_quantity * product_selling_price`
            - `balance` = `sum(buyer tradePayments for trade) - sum(vendor tradePayments for trade)`
        6.  Return the list of `TradeCalculationDto`.

### 3.3. Adapters (Infrastructure)

- Create an `adapters` package.

- **Inbound Adapter (Driving):**
    - Create an `in/web` subpackage.
    - `TradeDashboardController`:
        - Exposes the `GET /api/v1/dashboards/trade-calculations` endpoint.
        - Injects the `TradeCalculationUseCase`.
        - Delegates the call to the use case and returns the result.

- **Outbound Adapters (Driven):**
    - Create an `out` subpackage.
    - **Persistence Adapter (`out/persistence`):**
        - `TradePersistenceAdapter` implementing `GetTradesPort`.
        - Use Spring Data JPA.
        - `TradeEntity`: A JPA entity class mapping to the `trades` table.
        - `TradeRepository`: A `JpaRepository` interface for the `TradeEntity`.
        - The adapter will use the repository to fetch all trades and map them from `TradeEntity` to the `Trade` domain model. The method should be annotated with `@Async`.
    - **Hardcoded Data Adapter (`out/hardcoded`):**
        - `PaymentHardcodedAdapter` implementing `GetPaymentsPort`.
        - This adapter will not connect to a database.
        - It will return a hardcoded `List<Payment>`.
        - The method should be annotated with `@Async`.
        - Provide sample payment data for trade IDs 1 through 10, ensuring a mix of scenarios:
            - Positive balance (buyer tradePayments > vendor tradePayments)
            - Negative balance (buyer tradePayments < vendor tradePayments)
            - Zero balance (buyer tradePayments = vendor tradePayments)

## 4. Testing

- **Unit Tests:**
    - Test the `TradeCalculationService` logic.
    - Mock the `GetTradesPort` and `GetPaymentsPort` to provide controlled input data.
    - Verify that the calculations for `grossProfit`, `costOfGoods`, and `balance` are correct for various scenarios.
- **Integration Tests:**
    - Write a `@WebMvcTest` with `@AutoConfigureMockMvc`.
    - Test the `TradeDashboardController` endpoint (`/api/v1/dashboards/trade-calculations`).
    - Mock the `TradeCalculationUseCase` to isolate the web layer and verify that the controller returns the expected HTTP status and JSON structure.

## 5. Validation

- Build the project using `./gradlew build`.
- Run all tests using `./gradlew test`.
- All tests must pass. Iterate on the implementation until the build is green.
