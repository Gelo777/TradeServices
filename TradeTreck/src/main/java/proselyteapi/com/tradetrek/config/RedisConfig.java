package proselyteapi.com.tradetrek.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import proselyteapi.com.tradetrek.model.entity.Stock;

@Configuration
public class RedisConfig {

    @Bean
    public ReactiveRedisTemplate<String, Stock> reactiveRedisTemplateStock(
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

    @Bean
    public ReactiveRedisTemplate<String, Boolean> reactiveRedisTemplateApiKey(ReactiveRedisConnectionFactory factory) {
        return new ReactiveRedisTemplate<>(factory, RedisSerializationContext
                .<String, Boolean>newSerializationContext(new StringRedisSerializer())
                .key(new StringRedisSerializer())
                .value(new GenericToStringSerializer<>(Boolean.class))
                .build());
    }
}
