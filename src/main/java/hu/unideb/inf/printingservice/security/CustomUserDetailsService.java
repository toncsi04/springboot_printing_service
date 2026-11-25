package hu.unideb.inf.printingservice.security;

import hu.unideb.inf.printingservice.data.entity.UserEntity;
import hu.unideb.inf.printingservice.data.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = userRepository.findByFelhasznalonev(username)
                .orElseThrow(() -> new UsernameNotFoundException("Felhasználó nem található: " + username));

        var authorities = user.getJogosultsagok().stream()
                .map(jog -> new SimpleGrantedAuthority("ROLE_" + jog.getNev().toUpperCase()))
                .collect(Collectors.toSet());

        return new User(user.getFelhasznalonev(), user.getJelszo(), authorities);
    }
}