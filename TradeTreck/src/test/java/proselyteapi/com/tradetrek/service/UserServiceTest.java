package proselyteapi.com.tradetrek.service;

import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.security.crypto.password.*;
import proselyteapi.com.tradetrek.model.dto.*;
import proselyteapi.com.tradetrek.model.entity.*;
import proselyteapi.com.tradetrek.model.exception.*;
import proselyteapi.com.tradetrek.model.mapper.*;
import proselyteapi.com.tradetrek.repository.*;
import reactor.core.publisher.*;
import reactor.test.*;

import java.util.*;

import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterUser() {
        UserDto userDto = new UserDto();
        userDto.setUsername("testUser");
        userDto.setPassword("testPassword");

        User user = new User();
        user.setApiKey(UUID.randomUUID().toString());

        when(userMapper.toUser(userDto)).thenReturn(user);
        when(passwordEncoder.encode("testPassword")).thenReturn("encodedPassword");
        when(userRepository.save(user)).thenReturn(Mono.just(user));

        Mono<String> apiKeyMono = userService.registerUser(userDto);
        StepVerifier.create(apiKeyMono)
                .expectNextMatches(apiKey -> apiKey.equals(user.getApiKey()))
                .verifyComplete();
    }

    @Test
    void testGetApiKey() {
        Long userId = 1L;
        User user = new User();
        user.setApiKey("testApiKey");

        when(userRepository.findById(userId)).thenReturn(Mono.just(user));

        Mono<String> apiKeyMono = userService.getApiKey(userId);
        StepVerifier.create(apiKeyMono)
                .expectNextMatches(apiKey -> apiKey.equals(user.getApiKey()))
                .verifyComplete();
    }

    @Test
    void testGetUserByUsername() {
        String username = "testUser";
        User user = new User();
        user.setUsername(username);

        when(userRepository.findByUsername(username)).thenReturn(Mono.just(user));

        Mono<User> userMono = userService.getUserByUsername(username);
        StepVerifier.create(userMono)
                .expectNextMatches(u -> u.getUsername().equals(username))
                .verifyComplete();
    }

    @Test
    void testGetUserByUsernameNotFound() {
        String username = "nonExistentUser";

        when(userRepository.findByUsername(username)).thenReturn(Mono.empty());

        Mono<User> userMono = userService.getUserByUsername(username);
        StepVerifier.create(userMono)
                .expectErrorMatches(throwable -> throwable instanceof EntityNotFoundException)
                .verify();
    }
}
