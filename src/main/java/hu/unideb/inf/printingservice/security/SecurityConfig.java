package hu.unideb.inf.printingservice.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Jelszavak biztonságos hashelése
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // REST API-hoz és teszteléshez kikapcsoljuk
                .authorizeHttpRequests(auth -> auth
                        // Átlag felhasználóknak csak ennyit engedek meg
                        .requestMatchers(HttpMethod.POST, "/api/users").permitAll()
                        .requestMatchers("/", "/index.html", "/css/**", "/js/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/materials").permitAll()
                        .requestMatchers("/api/quotes/calculate").authenticated()


                        // Adminnak mindent hogy ne kelljen átírosgatni új funkciónál mindig
                        .anyRequest().hasRole("ADMIN")
                )
                .httpBasic(basic -> basic.authenticationEntryPoint((request, response, authException) -> {
                    response.sendError(401, "Hibás felhasználónév vagy jelszó");
                })); // Egyszerű Basic Auth hitelesítés Postmanhez és JS-hez

        return http.build();
    }
}