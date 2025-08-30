## Proposed Structure

````css
root
│
├── base-core
│    ├── (contains core-domain)
│    ├── (contains security-core)
│    ├── (contains other shared utilities, constants, exception handlers, logging config)
│
├── user-service
│    └── depends on base-core
│
├── transaction-service
│    └── depends on base-core
│
├── storage-service
│    └── depends on base-core
│
├── mlm-rank-service
│    └── depends on base-core
│
├── investment-service
│    └── depends on base-core
│
├── income-service
│    └── depends on base-core
│
└── nft-core
     └── depends on all services

````

## Dependency Graph
````css
     base-core
        ↑
  ┌─────┼────────────────────┐
  │     │                    │
user-service   transaction-service   storage-service   mlm-rank-service   investment-service   income-service

````