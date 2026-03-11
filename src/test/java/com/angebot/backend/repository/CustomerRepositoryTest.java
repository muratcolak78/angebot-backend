package com.angebot.backend.repository;

import com.angebot.backend.entity.Customer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository customerRepository;

    private Customer buildCustomer(UUID userId, String email) {
        Customer c = new Customer();
        c.setUserId(userId);
        c.setFirstName("Max");
        c.setLastName("Mustermann");
        c.setEmail(email);
        c.setPhone("0711123456");
        return c;
    }

    @Test
    @DisplayName("findByUserId - kullanıcıya ait tüm müşterileri döner")
    void shouldFindAllCustomersByUserId() {
        UUID userId = UUID.randomUUID();

        customerRepository.save(buildCustomer(userId, "max@test.de"));
        customerRepository.save(buildCustomer(userId, "anna@test.de"));
        customerRepository.save(buildCustomer(UUID.randomUUID(), "other@test.de"));

        List<Customer> result = customerRepository.findByUserId(userId);

        assertThat(result).hasSize(2);
        assertThat(result).allMatch(c -> c.getUserId().equals(userId));
    }

    @Test
    @DisplayName("findByUserIdAndId - doğru müşteriyi döner")
    void shouldFindCustomerByUserIdAndId() {
        UUID userId = UUID.randomUUID();
        Customer saved = customerRepository.save(buildCustomer(userId, "max@test.de"));

        Optional<Customer> result = customerRepository.findByUserIdAndId(userId, saved.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("max@test.de");
    }

    @Test
    @DisplayName("findByUserIdAndId - yanlış userId ile bulunamaz")
    void shouldNotFindCustomerWithWrongUserId() {
        UUID userId = UUID.randomUUID();
        Customer saved = customerRepository.save(buildCustomer(userId, "max@test.de"));

        Optional<Customer> result = customerRepository.findByUserIdAndId(UUID.randomUUID(), saved.getId());

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("findByIdAndUserId - doğru müşteriyi döner")
    void shouldFindCustomerByIdAndUserId() {
        UUID userId = UUID.randomUUID();
        Customer saved = customerRepository.save(buildCustomer(userId, "max@test.de"));

        Optional<Customer> result = customerRepository.findByIdAndUserId(saved.getId(), userId);

        assertThat(result).isPresent();
        assertThat(result.get().getFirstName()).isEqualTo("Max");
    }

    @Test
    @DisplayName("existsByUserIdAndEmail - email zaten varsa true döner")
    void shouldReturnTrueIfEmailExists() {
        UUID userId = UUID.randomUUID();
        customerRepository.save(buildCustomer(userId, "max@test.de"));

        boolean exists = customerRepository.existsByUserIdAndEmail(userId, "max@test.de");

        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("existsByUserIdAndEmail - email yoksa false döner")
    void shouldReturnFalseIfEmailNotExists() {
        UUID userId = UUID.randomUUID();

        boolean exists = customerRepository.existsByUserIdAndEmail(userId, "yok@test.de");

        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("existsByUserIdAndEmail - farklı userId ile false döner")
    void shouldReturnFalseIfUserIdDoesNotMatch() {
        UUID userId = UUID.randomUUID();
        customerRepository.save(buildCustomer(userId, "max@test.de"));

        boolean exists = customerRepository.existsByUserIdAndEmail(UUID.randomUUID(), "max@test.de");

        assertThat(exists).isFalse();
    }
}