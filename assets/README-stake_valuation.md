## Design a Configurable Stake Valuation System for a Multi-Schema Investment Platform

### 📘 Context:
You are building a platform where users can reserve investment stakes based on various **investment schemas** (e.g., fixed-price plans, tokenized assets, ROI-based instruments). Once reserved, the value of a stake may **fluctuate over time**, depending on the schema type and external market conditions.

Users should be able to:
- View the **current value** of their reserved stake.
- See the **valuation delta** (i.e., the change in value since reservation).
- The method of calculating the delta should be configurable per schema type, supporting multiple valuation models (e.g., fixed appreciation, external market-based price, custom business logic).


🎯 Requirements:
1. Stake Reservation System:
   - Users can reserve a stake at a specific value (reservedPrice) for a given schema.
   - Stake records are stored with metadata like reservation date, schema, and reserved price.
2. Dynamic Valuation System:
   - Stake value can change daily depending on schema rules.
   - The system should compute and display:
     - currentValue
     - valuationDelta = currentValue - reservedPrice
3. Configurable Valuation Strategy:
   - Each investment schema may require a different method to calculate current value.
   - Example strategies:
     - Fixed price increase per day
     - Real-time token market price from external API
     - ROI-based growth
   - The system must support plug-and-play strategies per schema.

4. Decoupled Architecture:
- Use extensible design (e.g., Strategy Pattern) to allow adding new valuation methods without touching core business logic.
- Ensure reusability, testability, and adherence to SOLID principles.

5. API Endpoint:
   - Provide a REST API for users to view
     - Stake ID
     - Reserved price
     - Current price
     - Valuation delta

## 📈 Example Scenario:
A user reserves a stake under `Token-X` schema at ₹192.79. Today, the market price of `Token-X` is ₹201.08.

The system should show:
- `reservedPrice`: `₹192.79`
- `currentValue`: `₹201.08`
- `valuationDelta`: `₹8.29`

---

## ✅ Step-by-Step Implementation Plan
### 1. Define an interface for valuation strategy
````java
public interface StakeValuationStrategy {
    boolean supports(InvestmentSchema schema);
    BigDecimal calculateValuationDelta(StakeReservation reservation);
}
````

### 2. Implement concrete strategies
Example: Default price appreciation strategy
````java
@Component
public class DefaultStakeValuationStrategy implements StakeValuationStrategy {

    @Override
    public boolean supports(InvestmentSchema schema) {
        return true; // fallback or for simple fixed-price schemes
    }

    @Override
    public BigDecimal calculateValuationDelta(StakeReservation reservation) {
        BigDecimal reservedPrice = reservation.getReservedPrice();
        BigDecimal currentPrice = reservation.getInvestmentSchema().getPrice(); // current price
        return currentPrice.subtract(reservedPrice);
    }
}
````
Example: Custom token-based strategy
````java
@Component
public class TokenStakeValuationStrategy implements StakeValuationStrategy {

    @Override
    public boolean supports(InvestmentSchema schema) {
        return "TOKEN".equalsIgnoreCase(schema.getSchemaBadge());
    }

    @Override
    public BigDecimal calculateValuationDelta(StakeReservation reservation) {
        // e.g., fetch price from external API or dynamic table
        BigDecimal marketPrice = fetchMarketPriceFromAPI(schema.getTitle());
        return marketPrice.subtract(reservation.getReservedPrice());
    }

    private BigDecimal fetchMarketPriceFromAPI(String token) {
        // call external service or mock it
        return new BigDecimal("205.00");
    }
}
````
### 3. Create a strategy resolver/service
````java
@Service
@RequiredArgsConstructor
public class StakeValuationService {

    private final List<StakeValuationStrategy> strategies;

    public BigDecimal computeValuationDelta(StakeReservation reservation) {
        return resolveStrategy(reservation.getInvestmentSchema())
                .calculateValuationDelta(reservation);
    }

    private StakeValuationStrategy resolveStrategy(InvestmentSchema schema) {
        return strategies.stream()
                .filter(s -> s.supports(schema))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No strategy found for schema: " + schema.getTitle()));
    }
}
````

### 4. Use in controller or DTO response
````java
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reservations")
public class StakeReservationController {

    private final StakeReservationService reservationService;
    private final StakeValuationService valuationService;

    @GetMapping("/{id}")
    public ResponseEntity<StakeReservationDto> getReservation(@PathVariable Long id) {
        StakeReservation reservation = reservationService.findById(id);

        BigDecimal valuationDelta = valuationService.computeValuationDelta(reservation);
        BigDecimal currentValue = reservation.getReservedPrice().add(valuationDelta);

        StakeReservationDto dto = StakeReservationDto.from(reservation, currentValue, valuationDelta);
        return ResponseEntity.ok(dto);
    }
}
````
### 5. Sample DTO
````java
public class StakeReservationDto {
    private Long id;
    private BigDecimal reservedPrice;
    private BigDecimal currentValue;
    private BigDecimal valuationDelta;

    public static StakeReservationDto from(StakeReservation r, BigDecimal currentValue, BigDecimal delta) {
        StakeReservationDto dto = new StakeReservationDto();
        dto.id = r.getId();
        dto.reservedPrice = r.getReservedPrice();
        dto.currentValue = currentValue;
        dto.valuationDelta = delta;
        return dto;
    }
}
````

---

##  1. Where to store `currentValue` of a stake?

### Option A: Store in DB (denormalized field)
Add a field in `StakeReservation`:
````java
private BigDecimal currentValue;
private BigDecimal valuationDelta;
````
- This field is updated daily via scheduled job or real-time hook.
- Pros
  - Fast to read for UI/API
  - Easy to sort/filter/export
- Cons
  - Needs regular update
  - Risk of stale data if job fails


### Option B: Compute on-the-fly (no DB field)
Compute `currentValue` dynamically whenever user fetches the stake via API:
````java
public BigDecimal getCurrentValue() {
    return valuationStrategy.calculate(stakeReservation);
}
````
- Pros
    - Always accurate
    - No need to sync
- Cons
    - Slightly more compute at runtime
    - Harder to filter/sort in DB

## ✅ Recommendation: Use both
- Store `currentValue` in DB as a snapshot, update daily or periodically.
- Also support dynamic calculation for real-time accuracy if needed.


##  2. Who decides and calculates `currentValue`?
Use Strategy Pattern with a `ValuationStrategy` interface:
````java
public interface ValuationStrategy {
    BigDecimal calculateCurrentValue(StakeReservation reservation);
}
````
Implement multiple strategies:
````java
public class FixedIncrementStrategy implements ValuationStrategy {
    public BigDecimal calculateCurrentValue(StakeReservation reservation) {
        // Add fixed 2% per day since reservation
    }
}

public class MarketPriceStrategy implements ValuationStrategy {
    public BigDecimal calculateCurrentValue(StakeReservation reservation) {
        // Fetch price from external API
    }
}

public class ROIModelStrategy implements ValuationStrategy {
    public BigDecimal calculateCurrentValue(StakeReservation reservation) {
        // Use ROI %, duration, etc.
    }
}
````
## ✅ 3. How to associate strategy with schema?
Option A: Enum + Registry
````java
enum ValuationModelType {
    FIXED_INCREMENT,
    MARKET_BASED,
    ROI_MODEL
}
````

In `InvestmentSchema`:
````java
@Enumerated(EnumType.STRING)
private ValuationModelType valuationModel;
````

And use a strategy resolver:
````java
@Component
public class ValuationStrategyResolver {
    @Autowired Map<ValuationModelType, ValuationStrategy> strategyMap;

    public BigDecimal compute(StakeReservation reservation) {
        InvestmentSchema schema = reservation.getSchema();
        ValuationStrategy strategy = strategyMap.get(schema.getValuationModel());
        return strategy.calculateCurrentValue(reservation);
    }
}
````

## ✅ 4. When/how to update the stored currentValue?
### Option A: Daily Scheduled Job
````java
@Scheduled(cron = "0 0 0 * * ?") // Every midnight
public void updateStakeValues() {
    List<StakeReservation> all = stakeRepository.findAllActive();
    for (StakeReservation stake : all) {
        BigDecimal currentValue = strategyResolver.compute(stake);
        stake.setCurrentValue(currentValue);
        stake.setValuationDelta(currentValue.subtract(stake.getReservedPrice()));
    }
    stakeRepository.saveAll(all);
}
````

### Option B: On-demand refresh (e.g., before displaying in UI)
- Call the strategy when needed (without updating DB).

## ✅ Summary
| Aspect               | Solution                                                         |
| -------------------- | ---------------------------------------------------------------- |
| **Storage**          | Store `currentValue` in `StakeReservation`, update periodically  |
| **Computation**      | Use `ValuationStrategy` interface with pluggable implementations |
| **Schema link**      | Store `ValuationModelType` in `InvestmentSchema`                 |
| **Update mechanism** | Scheduled job + real-time fallback (if needed)                   |
| **Benefits**         | Scalable, flexible, supports schema-specific logic               |















##