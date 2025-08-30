package com.trustai.notification_service.template.render;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
public class TemplateRenderer {
    public String render(String template, Map<String, String> props) {
        log.info("Starting template rendering");

        if (props == null || props.isEmpty()) {
            log.info("No properties provided. Returning original template.");
            return template;
        }

        log.debug("Original template before rendering: {}", template);
        log.debug("Properties to apply: {}", props);

        for (Map.Entry<String, String> entry : props.entrySet()) {
            //template = template.replace("{{" + entry.getKey() + "}}", entry.getValue());
            String placeholder = "{{" + entry.getKey() + "}}";
            String value = entry.getValue();
            log.debug("Replacing placeholder '{}' with value '{}'", placeholder, value);
            template = template.replace(placeholder, value);
        }
        log.info("Template rendering complete.");
        log.debug("Rendered template: {}", template);
        return template;
    }
}
