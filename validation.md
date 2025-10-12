AI Validation Prompt
Your Role: You are an expert software engineer and code reviewer specializing in Java, Spring Boot, and Hexagonal Architecture.
Your Task: I have provided you with the complete source code for a "Trade Calculations Dashboard API" project. Your mission is to perform a rigorous validation of this solution against the detailed requirements checklist below. For each item, you must analyze the code to confirm its presence and correctness.
Input: The complete source code of the Spring Boot project.
Output Format: Please provide a structured report. For each numbered item in the checklist, provide a status (✅ PASS, ❌ FAIL, or ⚠️ WARNING) followed by a brief, clear explanation. A FAIL should be used for clear violations of the requirements. A WARNING can be used for minor deviations or potential issues. Conclude with a final summary and an overall verdict.
Validation Checklist
1. Project Setup & Dependencies
   1.1. Build Tool: Verify that the project is configured using Gradle. Check for a build.gradle or build.gradle.kts file.
   1.2. Dependencies: Check the build.gradle file to ensure it includes all the following dependencies:
   spring-boot-starter-web
   spring-boot-starter-webflux
   spring-boot-starter-data-jpa
   postgresql driver
   flyway-core
   spring-boot-starter-test
   org.projectlombok:lombok (if used)
   1.3. Configuration: Inspect src/main/resources/application.yml (or .properties) to confirm:
   The application port is set to 8099.
   PostgreSQL datasource properties (url, username, password) are configured.
2. Database Schema and Data
   2.1. Flyway Migration:
   Confirm the existence of the migration script at src/main/resources/db/migration/V1__Create_trades_table.sql.
   Analyze the SQL in this file to verify the creation of a trades table.
   2.2. Table Schema: Check the V1__Create_trades_table.sql script to ensure the trades table has the exact columns, types, and constraints specified:
   id (BIGINT, Primary Key, Generated)
   buying_account_id (BIGINT, Not Null)
   vendor_account_id (BIGINT, Not Null)
   product_name (VARCHAR(255), Not Null)
   product_quantity (INT, Not Null)
   product_buying_price (DECIMAL(19, 2), Not Null)
   product_selling_price (DECIMAL(19, 2), Not Null)
   2.3. Seed Data: Verify the existence of a scripts/trades-data.sql file in the project root containing at least 10 INSERT statements for the trades table.
3. Hexagonal Architecture Implementation
   3.1. Project Structure: Analyze the package structure. Does it clearly separate domain, application, and adapters concerns?
   3.2. Domain (Core):
   Models: Confirm the existence of Trade and Payment domain models with fields that match the requirements.
   DTO: Confirm the existence of a TradeCalculationDto class or record with the fields: tradeId, grossProfit, costOfGoods, and balance.
   Ports (Interfaces):
   Check for a TradeCalculationUseCase interface in an application/ports/in package with the method List<TradeCalculationDto> getTradeCalculations();.
   Check for a GetTradesPort interface in an application/ports/out package with the method CompletableFuture<List<Trade>> getAllTrades();.
   Check for a GetPaymentsPort interface in an application/ports/out package with the method CompletableFuture<List<Payment>> getAllPayments();.
   3.3. Application (Use Case Implementation):
   Service Implementation: Locate the TradeCalculationService and verify it implements TradeCalculationUseCase.
   Asynchronous Execution: Check the main application class for the @EnableAsync annotation.
   Asynchronous Logic: Inside the getTradeCalculations method, verify that it:
   Calls getTradesPort.getAllTrades() and getPaymentsPort.getAllPayments() asynchronously.
   Correctly waits for both CompletableFutures to complete before proceeding.
   Calculation Logic: Crucially, verify the formulas used for the calculations are exactly as specified:
   grossProfit = product_quantity * product_buying_price
   costOfGoods = product_quantity * product_selling_price
   balance = sum of buyer payments for a given trade - sum of vendor payments for that same trade
   3.4. Adapters (Infrastructure):
   Inbound Adapter (Web):
   Find the TradeDashboardController in an adapters/in/web package.
   Verify it exposes a GET endpoint at the path /api/v1/dashboards/trade-calculations.
   Confirm it injects and calls the TradeCalculationUseCase.
   Outbound Adapter (Persistence):
   Find TradePersistenceAdapter in an adapters/out/persistence package and confirm it implements GetTradesPort.
   Check for a JPA TradeEntity and a TradeRepository extending JpaRepository.
   Verify the adapter uses the repository to fetch data and maps the TradeEntity to the domain Trade model.
   Confirm the getAllTrades method is annotated with @Async.
   Outbound Adapter (Hardcoded):
   Find PaymentHardcodedAdapter in an adapters/out/hardcoded package and confirm it implements GetPaymentsPort.
   Verify that it returns a hardcoded list of Payment objects and does not interact with a database.
   Inspect the hardcoded data to ensure it covers scenarios for positive, negative, and zero balances.
   Confirm the getAllPayments method is annotated with @Async.
4. Testing
   4.1. Unit Tests:
   Locate the unit tests for TradeCalculationService.
   Verify that the GetTradesPort and GetPaymentsPort dependencies are mocked (e.g., using Mockito's @Mock).
   Check that the tests assert the correctness of the grossProfit, costOfGoods, and balance calculations.
   4.2. Integration Tests:
   Locate the integration tests for TradeDashboardController.
   Verify the test class is annotated with @WebMvcTest and @AutoConfigureMockMvc.
   Check that the TradeCalculationUseCase is mocked (using @MockBean).
   Confirm the test performs a request to the endpoint and asserts the expected HTTP status (e.g., 200 OK) and the JSON response structure.
5. Build and Execution
   5.1. Build Success: While you cannot run the build yourself, review the project structure, Gradle files, and code for any obvious syntax errors or configuration issues that would cause ./gradlew build or ./gradlew test to fail. State if the project appears buildable and if all tests are likely to pass based on their implementation.
   Final Summary
   Please provide a brief summary of your findings. Conclude with a final verdict:
   PASS: The solution meets all specified requirements.
   PASS WITH WARNINGS: The solution is functional and meets most requirements, but there are minor deviations or areas for improvement.
   FAIL: The solution has critical deviations from the requirements, is incomplete, or has significant architectural flaws.
