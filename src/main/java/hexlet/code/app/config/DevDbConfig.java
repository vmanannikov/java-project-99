package hexlet.code.app.config;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("development")
public class DevDbConfig implements DBConfig {
    @Override
    public void setup() {

    }
}