package com.heamimont.salesstoreapi.repository;

import com.heamimont.salesstoreapi.model.Role;
import com.heamimont.salesstoreapi.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password123");
        testUser.setRole(Role.USER);

        // Clear previous data and insert a fresh test user
        entityManager.clear();
        entityManager.persist(testUser);
        entityManager.flush();
    }

    @Test
    void findByUsername_ShouldReturnUser_WhenUserExists() {
        Optional<User> found = userRepository.findByUsername(testUser.getUsername());

        assertThat(found)
                .isPresent()
                .hasValueSatisfying(user -> {
                    assertThat(user.getUsername()).isEqualTo(testUser.getUsername());
                    assertThat(user.getEmail()).isEqualTo(testUser.getEmail());
                });
    }

    @Test
    void findByUsername_ShouldReturnEmpty_WhenUserDoesNotExist() {
        Optional<User> found = userRepository.findByUsername("nonexistent");

        assertThat(found).isEmpty();
    }

    @Test
    void findByEmail_ShouldReturnUser_WhenUserExists() {
        Optional<User> found = userRepository.findByEmail(testUser.getEmail());

        assertThat(found)
                .isPresent()
                .hasValueSatisfying(user -> {
                    assertThat(user.getUsername()).isEqualTo(testUser.getUsername());
                    assertThat(user.getEmail()).isEqualTo(testUser.getEmail());
                });
    }

    @Test
    void findByEmail_ShouldReturnEmpty_WhenUserDoesNotExist() {
        Optional<User> found = userRepository.findByEmail("nonexistent@example.com");

        assertThat(found).isEmpty();
    }

    @Test
    void existsByUsername_ShouldReturnTrue_WhenUserExists() {
        boolean exists = userRepository.existsByUsernameIgnoreCase(testUser.getUsername());

        assertThat(exists).isTrue();
    }

    @Test
    void existsByUsername_ShouldReturnFalse_WhenUserDoesNotExist() {
        boolean exists = userRepository.existsByUsernameIgnoreCase("nonexistent");

        assertThat(exists).isFalse();
    }

    @Test
    void existsByEmail_ShouldReturnTrue_WhenUserExists() {
        boolean exists = userRepository.existsByEmailIgnoreCase(testUser.getEmail());

        assertThat(exists).isTrue();
    }

    @Test
    void existsByEmail_ShouldReturnFalse_WhenUserDoesNotExist() {
        boolean exists = userRepository.existsByEmailIgnoreCase("nonexistent@example.com");

        assertThat(exists).isFalse();
    }

    @Test
    void save_ShouldPersistUser_WhenValidUserProvided() {
        User newUser = new User();
        newUser.setUsername("newuser");
        newUser.setFirstName("New");
        newUser.setLastName("User");
        newUser.setEmail("new@example.com");
        newUser.setPassword("password123");
        newUser.setRole(Role.USER);

        User saved = userRepository.save(newUser);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getUsername()).isEqualTo(newUser.getUsername());
        assertThat(saved.getEmail()).isEqualTo(newUser.getEmail());

        User found = entityManager.find(User.class, saved.getId());
        assertThat(found).isNotNull();
        assertThat(found.getUsername()).isEqualTo(newUser.getUsername());
    }
}