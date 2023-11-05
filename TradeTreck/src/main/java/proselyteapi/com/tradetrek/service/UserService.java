package proselyteapi.com.tradetrek.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import proselyteapi.com.tradetrek.model.dto.RegistrationDto;
import proselyteapi.com.tradetrek.model.dto.UserDto;
import proselyteapi.com.tradetrek.model.entity.User;
import proselyteapi.com.tradetrek.model.exception.EntityNotFoundException;
import proselyteapi.com.tradetrek.model.mapper.UserMapper;
import proselyteapi.com.tradetrek.repository.UserRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private KafkaTemplate<String, RegistrationDto> kafkaTemplate;

    private final String topicName = "registrationTopic";

    public void sendRegistrationDetails(RegistrationDto registrationDto) {
        kafkaTemplate.send(topicName, registrationDto);
    }

    public Mono<String> registerUser(UserDto userDto) {
        User user = userMapper.toUser(userDto);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setApiKey(UUID.randomUUID().toString());

        kafkaTemplate.send("registrationTopic", RegistrationDto.builder()
            .email(userDto.getEmail())
            .lastName(userDto.getLastName())
            .firstName(userDto.getLastName()).build());

        return userRepository.save(user)
                .map(User::getApiKey)
                .doOnSuccess(apiKey -> log.info("Зарегистрирован пользователь: {}", user.getUsername()));
    }

    public Mono<String> getApiKey(Long id) {
        return userRepository.findById(id)
                .map(User::getApiKey)
                .doOnNext(apiKey -> log.info("Получен API-ключ для пользователя: {}", apiKey));
    }

    public Mono<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .switchIfEmpty(Mono.error(new EntityNotFoundException("Пользователь не найден")))
                .doOnNext(user -> log.info("Получен пользователь: {}", user.getUsername()));
    }
}

