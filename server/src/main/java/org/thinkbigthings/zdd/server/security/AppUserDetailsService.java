package org.thinkbigthings.zdd.server.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thinkbigthings.zdd.server.UserRepository;

@Service
public class AppUserDetailsService implements UserDetailsService {

    private UserDetailsMapper userDetailsMapper = new UserDetailsMapper();

    private UserRepository userRepository;

    public AppUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // if not found, UserDetailsService is supposed to throw UsernameNotFoundException instead of return null
        UserDetails userDetails = userRepository.findByUsername(username)
                .map(userDetailsMapper::apply)
                .filter(user -> ! user.getAuthorities().isEmpty())
                .orElseThrow(() -> new UsernameNotFoundException("No User with authorities were found: " + username));

        return userDetails;
    }

}
