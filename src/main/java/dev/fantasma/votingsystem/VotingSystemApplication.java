package dev.fantasma.votingsystem;

import dev.fantasma.votingsystem.data.models.Role;
import dev.fantasma.votingsystem.data.models.User;
import dev.fantasma.votingsystem.data.repositories.UserRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Set;

@SpringBootApplication
@EnableScheduling
public class VotingSystemApplication implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public VotingSystemApplication(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Value("${admin-name}")
    private String name;
    @Value("${admin-email}")
    private String email;
    @Value("${admin-password}")
    private String password;

    static void main(String[] args) {
        SpringApplication.run(VotingSystemApplication.class, args);
    }

    @Override
    public void run(String @NonNull ... args) throws Exception {
        if (!userRepository.existsByEmail(email)) {
            Set<Role> roles = new HashSet<>(Set.of(Role.ROLE_ADMIN));
            User user = new User();
            user.setName(name);
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(password));
            user.setRoles(roles);
            userRepository.save(user);
        }
    }
}
