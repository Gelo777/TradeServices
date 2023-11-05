package proselyteapi.com.tradeclient;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.reactive.config.EnableWebFlux;

@SpringBootApplication
@EnableWebFlux
@EnableScheduling
public class TradeClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(TradeClientApplication.class, args);
    }

}
