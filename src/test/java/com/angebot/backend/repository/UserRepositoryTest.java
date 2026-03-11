package com.angebot.backend.repository;

import com.angebot.backend.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User buildUser(String email) {
        User user = new User();
        user.setEmail(email);
        user.setPasswordHash("hashed_password");
        return user;
    }

    @Test
    @DisplayName("findByEmail - email ile kullanıcı bulunur")
    void shouldFindUserByEmail() {
        userRepository.save(buildUser("max@test.de"));

        Optional<User> result = userRepository.findByEmail("max@test.de");

        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("max@test.de");
    }

    @Test
    @DisplayName("findByEmail - yanlış email ile boş döner")
    void shouldReturnEmptyForWrongEmail() {
        userRepository.save(buildUser("max@test.de"));

        Optional<User> result = userRepository.findByEmail("yok@test.de");

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("existsByEmail - email varsa true döner")
    void shouldReturnTrueIfEmailExists() {
        userRepository.save(buildUser("max@test.de"));

        boolean exists = userRepository.existsByEmail("max@test.de");

        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("existsByEmail - email yoksa false döner")
    void shouldReturnFalseIfEmailNotExists() {
        boolean exists = userRepository.existsByEmail("yok@test.de");

        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("save - id otomatik atanır")
    void shouldAutoGenerateId() {
        User saved = userRepository.save(buildUser("max@test.de"));

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getId()).isInstanceOf(UUID.class);
    }

    @Test
    @DisplayName("email unique - aynı email iki kez kaydedilemez")
    void shouldNotSaveDuplicateEmail() {
        userRepository.save(buildUser("max@test.de"));

        org.junit.jupiter.api.Assertions.assertThrows(Exception.class, () -> {
            userRepository.saveAndFlush(buildUser("max@test.de"));
        });
    }
}