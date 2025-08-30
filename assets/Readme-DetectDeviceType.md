## Detect Device Type:

To get device details in a Spring Boot app, you typically extract them from the User-Agent HTTP header. This header includes info like:
- Device type (mobile/desktop)
- OS (Windows, Android, iOS, etc.)
- Browser (Chrome, Safari, Firefox, etc.)
- App version (if a mobile app sends it)

### ✅ Example: Extracting User-Agent and Parsing It
1. **Get User-Agent Header**

````java
String userAgent = request.getHeader("User-Agent");
````

Example Value:
````swift
Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 Chrome/114.0.0.0 Safari/537.36
````

## ✅ Option 1: Use UserAgentUtils or ua-parser
Add Maven Dependency:

For [uap-java](https://github.com/ua-parser/uap-java):   
````xml
<dependency>
  <groupId>com.github.ua-parser</groupId>
  <artifactId>uap-java</artifactId>
  <version>1.5.4</version>
</dependency>
````

Usage:
````java
import ua_parser.Parser;
import ua_parser.Client;

public class DeviceInfoUtil {

    private final Parser parser = new Parser();

    public String parseUserAgent(String userAgent) {
        Client client = parser.parse(userAgent);
        return String.format("OS: %s, Device: %s, Browser: %s",
                client.os.family,
                client.device.family,
                client.userAgent.family + " " + client.userAgent.major);
    }
}
````
> Output: OS: Windows, Device: Other, Browser: Chrome 114

## ✅ Option 2: Use Spring UserAgent Parser (Lightweight)

For basic use cases, you can use String.contains() or regex if you don’t want a full library.

````java
public String detectDevice(String userAgent) {
    if (userAgent == null) return "Unknown";

    if (userAgent.toLowerCase().contains("mobile")) return "Mobile";
    if (userAgent.toLowerCase().contains("tablet")) return "Tablet";
    if (userAgent.toLowerCase().contains("windows") || userAgent.toLowerCase().contains("mac")) return "Desktop";

    return "Unknown";
}
````


## ✅ Bonus: If Mobile App Is Used
You can ask the app to pass extra headers like:
````makefile
X-App-Version: 1.2.3
X-Device-Model: Pixel 7
X-Platform: Android
````

Then extract via:
````java
String platform = request.getHeader("X-Platform");
````
