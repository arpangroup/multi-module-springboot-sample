
## What is business value?
In an **MLM (Multi-Level Marketing) system, Business Value (BV)** — sometimes called B**usiness Volume**, **Bonus Volume**, or **Commissionable Volume** — is a point-based or monetary value assigned to a product purchase or transaction, used for calculating bonuses, commissions, and ranks.

### 🔍 Why BV is used instead of actual money?
Because MLM systems often operate in multiple countries or sell various products with different margins, BV standardizes earnings across products and regions. It allows:
- Equal commission calculations.
- Rank progress tracking.
- Compensation consistency.

###  📘 Example:
| Product   | Price (USD) | BV |
| --------- | ----------- | -- |
| Product A | \$100       | 80 |
| Product B | \$50        | 35 |


When a downline buys Product A:
- You don’t earn commission on $100 directly.
- Instead, your system says: “This generates 80 BV.”
- If you get 10% of downline BV, you earn 8 units of commission.

### 🧮 In Code (Java-like):
````java
double bv = product.getBusinessValue();
double commission = bv * 0.10; // 10% referral bonus
````

### 🔁 BV is used in:
- Direct & indirect bonuses
- Matching bonuses
- Rank upgrades
- Group sales target evaluations