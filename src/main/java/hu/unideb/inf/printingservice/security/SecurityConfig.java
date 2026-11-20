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
                        // 1. Nyilvános végpontok (regisztráció és frontend fájlok bárkinek elérhetők)
                        .requestMatchers(HttpMethod.POST, "/api/users").permitAll()
                        .requestMatchers("/", "/index.html", "/css/**", "/js/**").permitAll()

                        // 2. Csak ADMIN joggal elérhető végpontok (RBAC)
                        .requestMatchers("/api/jogok/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/users/**").hasRole("ADMIN")

                        // 3. Minden más végponthoz (pl. árajánlat, anyagok) bejelentkezés kell
                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults()); // Egyszerű Basic Auth hitelesítés Postmanhez és JS-hez

        return http.build();
    }
}