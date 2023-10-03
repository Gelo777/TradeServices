package proselyteapi.com.tradetrek.config;

import org.springframework.context.annotation.*;
import org.springframework.data.redis.connection.*;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.serializer.*;
import proselyteapi.com.tradetrek.model.entity.*;

@Configuration
public class RedisConfig {

    @Bean
    public ReactiveRedisTemplate<String, Stock> reactiveRedisTemplate(
            ReactiveRedisConnectionFactory connectionFactory) {
        RedisSerializationContext<String, Stock> serializationContext =
                RedisSerializationContext
                        .<String, Stock>newSerializationContext()
                        .key(StringRedisSerializer.UTF_8)
                        .value(new Jackson2JsonRedisSerializer<>(Stock.class))
                        .hashKey(StringRedisSerializer.UTF_8)
                        .hashValue(new Jackson2JsonRedisSerializer<>(Stock.class))
                        .build();

        return new ReactiveRedisTemplate<>(connectionFactory, serializationContext);
    }
}
