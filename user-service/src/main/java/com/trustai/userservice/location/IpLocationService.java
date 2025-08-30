package com.trustai.userservice.location;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class IpLocationService {
    private final RestTemplate restTemplate = new RestTemplate();
    private final Map<String, IpApiResponse> cache = new ConcurrentHashMap<>();

    public IpApiResponse fetchIpDetails(String ip) {
        return cache.computeIfAbsent(ip, this::callIpApi);
    }

    private IpApiResponse callIpApi(String ip) {
        log.info("Fetching IP Details for: {}", ip);
        if ("0:0:0:0:0:0:0:1".equals(ip) || "127.0.0.1".equals(ip)) {
            ip = "103.120.51.73"; // replace with known test IP or simulate in dev
        }
        /*try {
            String url = "http://ip-api.com/json/" + ip;
            //Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            ResponseEntity<IpApiResponse> response = restTemplate.getForEntity(url, IpApiResponse.class);
            return response.getBody();
        } catch (Exception e) {
            // fallback or log error
            return null;
        }*/
        return null;
    }
}
