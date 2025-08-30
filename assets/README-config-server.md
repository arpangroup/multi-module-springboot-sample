## ❓ If I provide both application.properties and a config server URL, which property takes precedence?
Or,
## ❓ What is the order of precedence for property sources in Spring Boot?

From highest to lowest:
1. Command-line arguments (--property=value)
2. Environment variables (SPRING_APPLICATION_JSON, etc.)
3. application.properties or application.yml in src/main/resources
4. Remote Config Server (via spring.config.import=configserver:)
5. application-default.properties


## ❓ Can I override config server properties with local ones?
Yes, but only by:
- Passing properties as command-line arguments
- Defining environment variables
- Using a custom `PropertySource` **loader** or conditional loading logic (advanced)


❓ Example: If both sources define bonus.referral.flat-amount, which value is used?
- `application.properties`: `bonus.referral.flat-amount=100`
- Config server: `bonus.referral.flat-amount=200`

➡️ Result: 200 is used, from the config server.