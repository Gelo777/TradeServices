package proselyteapi.com.tradetrek;

import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.scheduling.annotation.*;
import org.springframework.web.reactive.config.*;

@SpringBootApplication
@EnableWebFlux
@EnableScheduling
public class TradeTrekApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(TradeTrekApplication.class, args);
    }
}
