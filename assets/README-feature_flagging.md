## key-value based configuration 
- Stores configuration in a database (key -> value style).
- Loads once on application startup or first access.
- Caches the values in memory (e.g., using Map, @PostConstruct, or @Cacheable).
- Allows runtime access throughout the app without hitting the DB again.


### Step 1: DB Table
````java
CREATE TABLE app_config (
  config_key VARCHAR(100) PRIMARY KEY,
  config_value TEXT
);
````

### Step 2: Entity & Repository
````java
@Entity
@Table(name = "app_config")
public class AppConfig {
    @Id
    @Column(name = "config_key")
    private String key;

    @Column(name = "config_value")
    private String value;

    // getters and setters
}
````
````java
public interface AppConfigRepository extends JpaRepository<AppConfig, String> {
}
````

### Step 3: Service with Lazy-Loaded In-Memory Cache
````java
@Service
public class ConfigService {

    private final AppConfigRepository repository;
    private final Map<String, String> configMap = new ConcurrentHashMap<>();
    private boolean loaded = false;

    public ConfigService(AppConfigRepository repository) {
        this.repository = repository;
    }

    @PostConstruct
    public void loadConfig() {
        if (!loaded) {
            repository.findAll().forEach(config -> 
                configMap.put(config.getKey(), config.getValue()));
            loaded = true;
        }
    }

    public String get(String key) {
        return configMap.get(key);
    }

    public Optional<String> getOptional(String key) {
        return Optional.ofNullable(configMap.get(key));
    }

    // auto-convert values to int
    public int getInt(String key, int defaultValue) {
        String value = configMap.get(key);
        try {
            return value != null ? Integer.parseInt(value) : defaultValue;
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    // auto-convert values to boolean
    public boolean getBoolean(String key, boolean defaultValue) {
        String value = configMap.get(key);
        return value != null ? Boolean.parseBoolean(value) : defaultValue;
    }

    // auto-convert values to double
    public double getDouble(String key, double defaultValue) {
        String value = configMap.get(key);
        try {
            return value != null ? Double.parseDouble(value) : defaultValue;
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}
````

### Step 4: Use Anywhere in the App
````java
@Service
public class FeatureService {

    @Autowired
    private ConfigService configService;

    public void doSomething() {
        String threshold = configService.get("bonus.threshold");
        if (threshold != null) {
            // use config
        }
    }
}
````

### ✅ Usage Example
````java
int threshold = configService.getInt("bonus.threshold", 100);
boolean isEnabled = configService.getBoolean("feature.new_flow", false);
double rate = configService.getDouble("commission.rate", 0.1);
````

## Admin Controller to Refresh at Runtime
````java
@RestController
@RequestMapping("/admin/config")
public class ConfigAdminController {

    @Autowired
    private ConfigService configService;

    @PostMapping("/reload")
    public ResponseEntity<String> reloadConfig() {
        configService.loadConfig(); // or force reload
        return ResponseEntity.ok("Config reloaded");
    }
}
````

---

## HTML UI to display the Configs

````text
generate a Ui using HTML and pure javascript 
which will load all the configs and render it in 
html, if its value is boolean it should show a 
toggle button, if value is int or double or 
number or string it should show  a input box 
and there will be a update button at last, 
which will update the changed value to DB
````

### ✅ 1. HTML + JavaScript UI
````html
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <title>Config Editor</title>
  <style>
    body { font-family: sans-serif; margin: 20px; }
    .config-item { margin-bottom: 10px; }
    label { display: inline-block; width: 200px; }
    input[type="text"] { width: 200px; }
  </style>
</head>
<body>
  <h2>Configuration Editor</h2>
  <div id="configContainer"></div>
  <button onclick="updateConfigs()">Update Configs</button>

  <script>
    const configState = {};

    async function loadConfigs() {
      const response = await fetch('/api/configs');
      const data = await response.json();

      const container = document.getElementById('configContainer');
      container.innerHTML = '';

      Object.entries(data).forEach(([key, value]) => {
        const div = document.createElement('div');
        div.className = 'config-item';

        const label = document.createElement('label');
        label.textContent = key;

        let input;

        if (value === 'true' || value === 'false') {
          // Boolean toggle
          input = document.createElement('input');
          input.type = 'checkbox';
          input.checked = (value === 'true');
          input.onchange = () => configState[key] = input.checked.toString();
        } else {
          // Input for numbers and strings
          input = document.createElement('input');
          input.type = 'text';
          input.value = value;
          input.oninput = () => configState[key] = input.value;
        }

        // Initial state
        configState[key] = value;

        div.appendChild(label);
        div.appendChild(input);
        container.appendChild(div);
      });
    }

    async function updateConfigs() {
      const response = await fetch('/api/configs/update', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(configState)
      });

      if (response.ok) {
        alert('Configs updated successfully');
      } else {
        alert('Failed to update configs');
      }
    }

    loadConfigs();
  </script>
</body>
</html>
````

### ✅ 2. Expected Backend Endpoints
**/api/configs (GET)**
````bash
{
  "bonus.threshold": "100",
  "feature.new_flow": "true",
  "commission.rate": "0.15"
}
````
**/api/configs/update (POST)**
````bash
{
  "bonus.threshold": "120",
  "feature.new_flow": "false",
  "commission.rate": "0.20"
}
````