package ru.greyson.prices_analyzer.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.greyson.prices_analyzer.models.User;
import ru.greyson.prices_analyzer.repositories.UserRepository;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class UserJWTService implements UserDetailsService {
    private final UserRepository userRepository;

    @Autowired
    public UserJWTService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByLogin(username);

        if (user.isEmpty())
            throw new UsernameNotFoundException("User not found");

        return new org.springframework.security.core.userdetails.User(
                user.get().getLogin(),
                user.get().getPassword(),
                user.get().getAuthorities()
        );
    }
}