# Listener Vs. Dispatcher

## ✅ 1. Listener

A listener is a class (or method) annotated with `@EventListener` that reacts directly to a specific event.
 
````java
@Component
public class ReferralBonusEventListener {
    @EventListener
    public void handleUserRegistered(UserRegisteredEvent event) {
        // handle referral bonus
    }
}
````
**✅ When to use:**
- You want modular, independent, and reusable logic.
- Event handlers are not dependent on order or sequence.

## ✅ 2. Dispatcher
A dispatcher is a centralized handler that receives the event and **manually delegates** to other services in a **defined order**.
````java
@Component
@RequiredArgsConstructor
public class UserRegisteredDispatcher {
    private final UserHierarchyService hierarchyService;
    private final ReferralBonusService referralService;

    @EventListener
    public void handle(UserRegisteredEvent event) {
        hierarchyService.updateHierarchy(event.getReferrerId(), event.getRefereeId());
        referralService.createPendingBonus(event.getReferrerId(), event.getRefereeId(), event.getTriggerType());
    }
}
````
**✅ When to use:**
- You want explicit control over execution order.
- You want to avoid potential conflicts from multiple listeners on the same event.
- Easier to debug and unit test one central place.


## 🔍 Summary Table:

| Feature                 | Listener                         | Dispatcher                             |
| ----------------------- | -------------------------------- | -------------------------------------- |
| Modularity              | High (each listener is isolated) | Lower (centralized logic)              |
| Execution Order Control | Tricky, uses `@Order`            | Easy, you write the sequence manually  |
| Coupling                | Low (loosely coupled)            | High (central control of all handling) |
| Reusability             | High                             | Lower                                  |
| Debuggability           | Can be harder (scattered logs)   | Easier (central log flow)              |


























