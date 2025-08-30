## Fetch IP Details

## 🌐 In Production (Behind Proxy/Load Balancer)
````java
public String getClientIp(HttpServletRequest request) {
    String xfHeader = request.getHeader("X-Forwarded-For");
    if (xfHeader != null) {
        return xfHeader.split(",")[0];
    }
    return request.getRemoteAddr();
}
````


## ✅ Option 1: Use a Free External API (Easy and Quick)

### Example: [ip-api.com](http://ip-api.com/) (No API key needed)
````java
public record IpLocation(String country, String city) {}

public IpLocation getLocationFromIp(String ip) {
    try {
        String url = "http://ip-api.com/json/" + ip;
        RestTemplate restTemplate = new RestTemplate();
        Map<String, Object> response = restTemplate.getForObject(url, Map.class);

        if ("success".equals(response.get("status"))) {
            String country = (String) response.get("country");
            String city = (String) response.get("city");
            return new IpLocation(country, city);
        }
    } catch (Exception e) {
        // fallback or log error
    }
    return new IpLocation("Unknown", "Unknown");
}
````
>  ⚠️ Free tier has request limits and may block repeated calls from the same IP. Cache results or use paid plan if needed.

> The free tier of ip-api.com lets you make up to 45 requests per minute – exceeding this will result in HTTP 429 “Too Many Requests”, and repeated abuse can even temporarily ban your IP

## ✅ Option 2: MaxMind GeoIP2 Local Database (Reliable & Offline)
1. Download Free **GeoLite2**:
   - Sign up at https://www.maxmind.com
   - Download: GeoLite2-City.mmdb
2. Add Maven Dependency:
    ````xml
    <dependency>
      <groupId>com.maxmind.geoip2</groupId>
      <artifactId>geoip2</artifactId>
      <version>4.1.0</version>
    </dependency>
    ````
3. Load Database and Query IP:
````java
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.model.CityResponse;

public class IpLocationService {

    private final DatabaseReader dbReader;

    public IpLocationService() throws IOException {
        File database = new File("GeoLite2-City.mmdb");
        dbReader = new DatabaseReader.Builder(database).build();
    }

    public IpLocation getLocation(String ip) {
        try {
            InetAddress ipAddress = InetAddress.getByName(ip);
            CityResponse response = dbReader.city(ipAddress);
            String country = response.getCountry().getName();
            String city = response.getCity().getName();
            return new IpLocation(country, city);
        } catch (Exception e) {
            return new IpLocation("Unknown", "Unknown");
        }
    }
}
````



## ✅ Option 3: Use a Commercial IP Intelligence API (Best Accuracy)

| Provider                                                          | Features                     | Notes                     |
| ----------------------------------------------------------------- | ---------------------------- | ------------------------- |
| [ipinfo.io](https://ipinfo.io/)                                   | IP to City/Country/ISP       | Free tier up to 50k/month |
| [ipstack.com](https://ipstack.com/)                               | Detailed location & currency | Requires API key          |
| [abstractapi.com](https://www.abstractapi.com/ip-geolocation-api) | Lightweight IP geolocation   | Free up to 20k req/month  |


## 💡 Recommendation
For most Spring Boot apps:
- Use **MaxMind GeoLite2** for performance + control (self-hosted)
- Or use **ip-api.com** for small-scale apps (easy + no key)