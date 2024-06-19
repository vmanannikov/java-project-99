package hexlet.code.config;

import net.datafaker.Faker;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class AppConfig {
    @Bean
    public Faker getFaker() {
        return new Faker();
    }
}


