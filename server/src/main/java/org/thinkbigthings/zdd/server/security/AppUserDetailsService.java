package org.thinkbigthings.zdd.server.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.thinkbigthings.zdd.server.UserRepository;

@Service
public class AppUserDetailsService implements UserDetailsService {

    private UserDetailsMapper userDetailsMapper = new UserDetailsMapper();

    private UserRepository userRepository;

    public AppUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        return userRepository.findByUsername(username)
                .map(userDetailsMapper::apply)
                .filter(user -> ! user.getAuthorities().isEmpty())
                .orElseThrow(() -> new UsernameNotFoundException("No User with authorities were found: " + username));
    }

}
