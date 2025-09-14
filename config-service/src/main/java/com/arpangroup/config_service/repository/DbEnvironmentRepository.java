package com.arpangroup.config_service.repository;

import com.arpangroup.config_service.entity.ConfigProperty;
import org.springframework.cloud.config.server.environment.EnvironmentRepository;
import org.springframework.cloud.config.environment.Environment;
import org.springframework.cloud.config.environment.PropertySource;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class DbEnvironmentRepository implements EnvironmentRepository {

    private final ConfigPropertyRepository repository;

    public DbEnvironmentRepository(ConfigPropertyRepository repository) {
        this.repository = repository;
    }

    @Override
    public Environment findOne(String application, String profile, String label) {
        profile = profile.toLowerCase();
        //List<ConfigProperty> props = repository.findByApplicationAndProfile(application, profile);
        List<ConfigProperty> props = repository.findAll();

        Map<String, Object> map = new HashMap<>();
        for (ConfigProperty p : props) {
            map.put(p.getKey(), p.getValue());
        }

        PropertySource propertySource = new PropertySource("database", map);

        Environment environment = new Environment(application, new String[]{profile}, label, null, null);
        environment.add(propertySource);

        return environment;
    }
}
