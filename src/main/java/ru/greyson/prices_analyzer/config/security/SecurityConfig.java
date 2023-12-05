package ru.greyson.prices_analyzer.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import ru.greyson.prices_analyzer.security.JWTFilter;
import ru.greyson.prices_analyzer.services.UserJWTService;

@Configuration
@EnableMethodSecurity
@EnableWebSecurity
public class SecurityConfig {
    private final JWTFilter jwtFilter;
    private final UserJWTService userJWTService;

    @Autowired
    public SecurityConfig(JWTFilter jwtFilter, UserJWTService userJWTService) {
        this.jwtFilter = jwtFilter;
        this.userJWTService = userJWTService;
    }

    public static final String[] ENDPOINTS_WHITELIST = {
            "/static/**",
            //"/",
            "/auth/login",
            "/auth/register",
            //"/index"
    };

    public static final String[] ENDPOINTS_ADMIN = {
            "/admin/**"
    };

    private static final String LOGIN_URL = "/auth/login";
    private static final String HOME_PAGE = "/home";
    private static final String LOGOUT_URL = "/auth/logout";

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers(ENDPOINTS_WHITELIST).permitAll()
                        .requestMatchers(ENDPOINTS_ADMIN).hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage(LOGIN_URL)
                        .loginProcessingUrl(LOGIN_URL)
                        .usernameParameter("login")
                        .passwordParameter("password")
                        .defaultSuccessUrl(HOME_PAGE, true)
                )
                .logout(logout -> logout
                        .logoutUrl(LOGOUT_URL)
                        .logoutSuccessUrl(LOGIN_URL)
                )
                .sessionManagement(sessionManagement -> sessionManagement
                        .sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userJWTService);
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}