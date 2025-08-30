## Email / Phone Verification
verifying via **email link** is more secure and user-friendly than asking users to manually copy codes. Let’s walk through how to handle **email link verification** securely and cleanly.

## ✅ Key Differences (Link vs. Code)
| Type     | Verification Input     | Expiry-Safe | Secure from replay?         | UX     |
| -------- | ---------------------- | ----------- | --------------------------- | ------ |
| Code     | `email + 6-digit code` | ✅           | Needs manual expiration     | Medium |
| **Link** | One-click `token`      | ✅ Easier    | ✅ Easier to make single-use | ✅ Best |


## ✅ Recommended Approach for Email Link Verification
### Option 1: Add early availability check API
````http
GET /api/auth/availability?username=arpan&email=arpan@example.com
````

````json
{
  "usernameAvailable": false,
  "emailAvailable": true
}
````
Use this in frontend before proceeding to request email verification.

### ✅ Option 2: Collect all registration data before email verification
- User fills form (username, password, email)
- Backend validates username/email availability
- Backend stores this data in pending_registrations or verification_tokens
- Email is verified
- Once verified, backend promotes record to users
This avoids last-step failures and improves UX.

0. **Check critical constraints (like username) before email verification:**
1. **Store a UUID-based token in DB, not a short numeric code.**
2. **Send a link like:**
3. **When user clicks:**
   - Backend finds token in DB
   - Marks it `verified = true` if valid and not expired
   - Deletes or invalidates token

## ✅ Table Design: verification_tokens
````sql
CREATE TABLE verification_tokens (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    target VARCHAR(255) NOT NULL, -- email or mobile
    type VARCHAR(20) NOT NULL CHECK (type IN ('EMAIL', 'MOBILE')),
    token VARCHAR(255) NOT NULL, -- UUID string
    expires_at TIMESTAMP NOT NULL,
    verified BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (token)
);
````

## ✅ Email Content Example
````text
Subject: Verify your email for MyApp

Hi [user],

Please verify your email by clicking the link below:

https://myapp.com/api/auth/verify-email?token=6f9a6a77-445d-47cb-bd02-abc123def456

This link will expire in 15 minutes.

Regards,
MyApp Team
````

## ✅ Java Implementation Summary
1. **Generate and store token**
````java
String token = UUID.randomUUID().toString();
VerificationToken vt = new VerificationToken();
vt.setTarget(email);
vt.setType(VerificationType.EMAIL);
vt.setToken(token);
vt.setExpiresAt(LocalDateTime.now().plusMinutes(15));
vt.setVerified(false);
repository.save(vt);
````
2. **Send verification link**
````java
String link = "https://yourdomain.com/api/auth/verify-email?token=" + token;
emailService.sendEmail(email, "Verify your email", "Click: " + link);
````
3. **REST API to handle click**
````java
@GetMapping("/api/auth/verify-email")
public ResponseEntity<?> verifyByToken(@RequestParam String token) {
    VerificationToken vt = repo.findByToken(token)
        .orElseThrow(() -> new IllegalArgumentException("Invalid token"));

    if (vt.isVerified()) throw new IllegalStateException("Already verified");
    if (vt.getExpiresAt().isBefore(LocalDateTime.now()))
        throw new IllegalStateException("Token expired");

    vt.setVerified(true);
    repo.save(vt);
    return ResponseEntity.ok("Email verified successfully");
}
````
4. sssds