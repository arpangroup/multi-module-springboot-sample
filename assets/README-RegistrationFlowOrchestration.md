#  Registration Flow Orchestration

## 🔁 Core Idea:
Model the registration process as a multi-step flow with explicit steps:
1. Availability Check
2. Email Verification
3. Mobile Verification (Optional)
4. Final Registration


## 🧠 Implementation Approaches

### ✅ Option 1: Strategy Pattern + Orchestrator
Each step is a RegistrationStep strategy, and an Orchestrator executes them in order.

````java
public interface RegistrationStep {
    void execute(RegistrationContext context);
}
````
**Orchestrator**
````java
@Service
@RequiredArgsConstructor
public class RegistrationFlowManager {

    private final List<RegistrationStep> steps;

    public void register(RegistrationRequest request) {
        RegistrationContext context = new RegistrationContext(request);
        for (RegistrationStep step : steps) {
            step.execute(context);
        }
    }
}
````
OR
````java
// STEP 1: Registration Context
public class RegistrationContext {
    private final RegistrationRequest request;
    private boolean emailVerified;
    private boolean usernameAvailable;

    public RegistrationContext(RegistrationRequest request) {
        this.request = request;
    }

    public RegistrationRequest getRequest() { return request; }
    public boolean isEmailVerified() { return emailVerified; }
    public void setEmailVerified(boolean emailVerified) { this.emailVerified = emailVerified; }
    public boolean isUsernameAvailable() { return usernameAvailable; }
    public void setUsernameAvailable(boolean usernameAvailable) { this.usernameAvailable = usernameAvailable; }
}

// STEP 2: RegistrationStep Interface
public interface RegistrationStep {
    void execute(RegistrationContext context);
}

// STEP 3: Availability Check Step
@Component
@Order(1)
public class AvailabilityCheckStep implements RegistrationStep {

    private final UserRepository userRepo;

    public AvailabilityCheckStep(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public void execute(RegistrationContext context) {
        boolean usernameAvailable = !userRepo.existsByUsername(context.getRequest().getUsername());
        if (!usernameAvailable) {
            throw new IllegalArgumentException("Username already taken");
        }
        context.setUsernameAvailable(true);
    }
}

// STEP 4: Email Verification Check Step
@Component
@Order(2)
public class EmailVerificationCheckStep implements RegistrationStep {

    private final VerificationTokenRepository tokenRepo;

    public EmailVerificationCheckStep(VerificationTokenRepository tokenRepo) {
        this.tokenRepo = tokenRepo;
    }

    @Override
    public void execute(RegistrationContext context) {
        var token = tokenRepo.findByTargetAndType(
                context.getRequest().getEmail(), VerificationType.EMAIL
        ).orElseThrow(() -> new IllegalArgumentException("No email verification record found"));

        if (!token.isVerified()) {
            throw new IllegalStateException("Email not verified");
        }

        context.setEmailVerified(true);
    }
}

// STEP 5: Final Registration Step
@Component
@Order(3)
public class FinalizeRegistrationStep implements RegistrationStep {

    private final UserRepository userRepo;
    private final PasswordEncoder encoder;

    public FinalizeRegistrationStep(UserRepository userRepo, PasswordEncoder encoder) {
        this.userRepo = userRepo;
        this.encoder = encoder;
    }

    @Override
    public void execute(RegistrationContext context) {
        RegistrationRequest request = context.getRequest();
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPasswordHash(encoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setMobile(request.getMobile());
        user.setReferralCode(request.getReferralCode());
        user.setCreatedAt(LocalDateTime.now());
        userRepo.save(user);
    }
}

// STEP 6: Flow Orchestrator
@Service
public class RegistrationFlowManager {

    private final List<RegistrationStep> steps;

    public RegistrationFlowManager(List<RegistrationStep> steps) {
        this.steps = steps;
    }

    public void registerUser(RegistrationRequest request) {
        RegistrationContext context = new RegistrationContext(request);
        for (RegistrationStep step : steps) {
            step.execute(context);
        }
    }
}

// DTO
public class RegistrationRequest {
    private String username;
    private String password;
    private String confirmPassword;
    private String email;
    private String mobile;
    private String referralCode;
    // Getters/setters omitted for brevity
}
````
✅ Here's a modular RegistrationFlowManager with pluggable steps like:
1. AvailabilityCheckStep
2. EmailVerificationCheckStep
3. FinalizeRegistrationStep

Each step implements RegistrationStep and is automatically wired in the correct order via `@Order`.

You can now easily add more steps (e.g., `MobileVerificationStep`, `OAuthMappingStep`) by implementing the interface and ordering them properly.

### MOBILE VERIFICATION SUPPORT
````java
@Component
@Order(2)
public class MobileVerificationCheckStep implements RegistrationStep {

    private final VerificationTokenRepository tokenRepo;

    public MobileVerificationCheckStep(VerificationTokenRepository tokenRepo) {
        this.tokenRepo = tokenRepo;
    }

    @Override
    public void execute(RegistrationContext context) {
        String mobile = context.getRequest().getMobile();
        if (mobile == null || mobile.isBlank()) return; // skip if not provided

        var token = tokenRepo.findByTargetAndType(mobile, VerificationType.MOBILE)
            .orElseThrow(() -> new IllegalArgumentException("Mobile not verified"));

        if (!token.isVerified()) {
            throw new IllegalStateException("Mobile verification required");
        }
    }
}
````

### OAUTH LOGIN FLOW INTEGRATION (BASIC IDEA)
````java
@RestController
@RequestMapping("/oauth")
public class OAuthController {

    private final UserRepository userRepo;

    public OAuthController(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginWithOAuth(@RequestBody OAuthRequest request) {
        // Example input: oauthProvider=GOOGLE, oauthId=abc123, email=user@gmail.com

        Optional<User> existingUser = userRepo.findByOauthProviderAndOauthId(
            request.getOauthProvider(), request.getOauthId()
        );

        User user = existingUser.orElseGet(() -> {
            User newUser = new User();
            newUser.setOauthProvider(request.getOauthProvider());
            newUser.setOauthId(request.getOauthId());
            newUser.setEmail(request.getEmail());
            newUser.setEmailVerified(true);
            newUser.setCreatedAt(LocalDateTime.now());
            return userRepo.save(newUser);
        });

        // Generate JWT or session here
        return ResponseEntity.ok("Login success");
    }
}
public class OAuthRequest {
    private String oauthProvider;
    private String oauthId;
    private String email;
    // Optional: name, avatar URL
    // Getters/setters
}
````



## ✅ Option 2: Orchestrated Service Layer (Simple + Practical)
````java
@Service
public class RegistrationService {

    public void checkAvailability(RegistrationRequest request) { ... }

    public void requestVerificationCode(String email) { ... }

    public void verifyEmailCode(String email, String code) { ... }

    public void completeRegistration(RegistrationRequest request) { ... }
}
````