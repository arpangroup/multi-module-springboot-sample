## 🧪 Test Scenario: Seller at Level 3 Sells a Product
Assumptions:
- Simplified rank and team rebate config for predictable calculations.
- Users:
  - U1 (Top - Level 0, Rank: RANK_4)
  - U2 (Level 1, Rank: RANK_3)
  - U3 (Level 2, Rank: RANK_2)
  - U4 (Seller - Level 3, Rank: RANK_2)


### Config (Simplified for testing):
````sql
-- Rank Commission Rates
RANK_2 → 10% (0.10)
RANK_3 → 15% (0.15)
RANK_4 → 20% (0.20)

-- Team Income Percentages
RANK_2: {1: 10%, 2: 5%, 3: 2%}
RANK_3: {1: 15%, 2: 7%, 3: 3%}
RANK_4: {1: 20%, 2: 10%, 3: 5%}
````

### Test Data Initialization:
- Product sold by U4 worth 100.00
- Wallets before:
  - U1: 500
  - U2: 400
  - U3: 300
  - U4: 200

### Expected Calculations:
- Seller U4:
  - Rank: RANK_2 → commission rate = 10%
  - Daily income = 100 × 0.10 = 10.00
- Team Income:
  - U3 (Level 1 from U4): RANK_2 → 10% of 10.00 = 1.00
  - U2 (Level 2): RANK_3 → 7% of 10.00 = 0.70
  - U1 (Level 3): RANK_4 → 5% of 10.00 = 0.50

### Wallets after:
- U1: 500.50
- U2: 400.70
- U3: 301.00
- U4: 210.00


````scss
   U1 (Rank 3, Level 3)
        │
   U2 (Rank 2, Level 2)
        │
   U3 (Rank 2, Level 1)
        │
   U4 (Seller, Rank 2)
````
- U4 is the seller who made a sale.
- U3 is the direct upline (depth 1) of U4.
- U2 is U3's upline (depth 2 from U4).
- U1 is U2's upline (depth 3 from U4).

### UserHierarchy entries:

| id | parent\_user\_id | child\_user\_id | depth |
| -- |------------------| --------------- | ----- |
| 1  | root             | u4.getId()      | 3     |
| 2  | u2.getId()       | u4.getId()      | 2     |
| 3  | u3.getId()       | u4.getId()      | 1     |


### Visual (Tree Format):**

````scss
U1 (Rank 3)
   └──(depth 2)
       └── U3 (Seller, Rank 2)
            └──(depth 1)
                └── U2 (Rank 2)
````

### Or reversed as "downline" from seller:
````scss
Seller U3
├── U2 (depth 1)
└── U1 (depth 2)
````