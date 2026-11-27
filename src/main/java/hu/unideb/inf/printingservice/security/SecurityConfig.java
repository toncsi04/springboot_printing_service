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
import org.springframework.web.bind.annotation.GetMapping;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // REST API-hoz és teszteléshez kikapcsoljuk
                .authorizeHttpRequests(auth -> auth
                        // Mindenkinek
                        .requestMatchers("/", "/index.html", "/css/**", "/js/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/users").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/materials").permitAll()

                        //Bejelentkezett felhasznaloknak arkalkulacios jogai
                        .requestMatchers("/api/quotes/calculate").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/quotes/my").authenticated()
                        .requestMatchers("/api/quotes/my/**").authenticated()

                        // Adminnak mindent hogy ne kelljen átírosgatni új funkciónál mindig
                        .anyRequest().hasRole("ADMIN")
                )
                .httpBasic(basic -> basic.authenticationEntryPoint((request, response, authException) -> {
                    response.sendError(401, "Hibás felhasználónév vagy jelszó");
                })); // Egyszerű Basic Auth hitelesítés Postmanhez és JS-hez

        return http.build();
    }
}