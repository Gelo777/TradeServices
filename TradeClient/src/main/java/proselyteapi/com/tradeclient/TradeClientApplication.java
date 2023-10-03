package proselyteapi.com.tradeclient;

import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.scheduling.annotation.*;
import org.springframework.web.reactive.config.*;

@SpringBootApplication
@EnableWebFlux
@EnableScheduling
public class TradeClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(TradeClientApplication.class, args);
    }

}
