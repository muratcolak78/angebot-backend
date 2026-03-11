package com.angebot.backend.repository;

import com.angebot.backend.entity.Settings;
import com.angebot.backend.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class SettingsRepositoryTest {

    @Autowired
    private SettingsRepository settingsRepository;

    @Autowired
    private UserRepository userRepository;

    private User buildUser() {
        User user = new User();
        user.setEmail(UUID.randomUUID() + "@test.de");
        user.setPasswordHash("hashed_password");
        return userRepository.save(user);
    }

    private Settings buildSettings(User user) {
        Settings s = new Settings();
        s.setUser(user);
        s.setFirstName("Max");
        s.setLastName("Mustermann");
        s.setCompanyName("Maler GmbH");
        s.setEmail("max@maler.de");
        s.setPhone("0711123456");
        s.setStreet("Bahnhofstraße");
        s.setHouseNr("12");
        s.setPlz("70173");
        s.setOrt("Stuttgart");
        return s;
    }

    @Test
    @DisplayName("findByUserId - kullanıcıya ait settings döner")
    void shouldFindSettingsByUserId() {
        User user = buildUser();
        settingsRepository.save(buildSettings(user));

        Optional<Settings> result = settingsRepository.findByUserId(user.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getCompanyName()).isEqualTo("Maler GmbH");
        assertThat(result.get().getUser().getId()).isEqualTo(user.getId());
    }

    @Test
    @DisplayName("findByUserId - yanlış userId ile boş döner")
    void shouldReturnEmptyForWrongUserId() {
        User user = buildUser();
        settingsRepository.save(buildSettings(user));

        Optional<Settings> result = settingsRepository.findByUserId(UUID.randomUUID());

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("findByUserId - kayıt yoksa boş döner")
    void shouldReturnEmptyWhenNoSettings() {
        Optional<Settings> result = settingsRepository.findByUserId(UUID.randomUUID());

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("save - tüm alanlar doğru kaydedilir")
    void shouldSaveAllFields() {
        User user = buildUser();
        settingsRepository.save(buildSettings(user));

        Settings found = settingsRepository.findByUserId(user.getId()).orElseThrow();

        assertThat(found.getFirstName()).isEqualTo("Max");
        assertThat(found.getLastName()).isEqualTo("Mustermann");
        assertThat(found.getPhone()).isEqualTo("0711123456");
        assertThat(found.getStreet()).isEqualTo("Bahnhofstraße");
        assertThat(found.getPlz()).isEqualTo("70173");
        assertThat(found.getOrt()).isEqualTo("Stuttgart");
    }
}