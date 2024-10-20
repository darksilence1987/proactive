package org.xhite.proactive.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.xhite.proactive.user.AppUser;
import org.xhite.proactive.user.UserRepository;
import org.xhite.proactive.user.UserStatus;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

            AppUser user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        if (!user.getStatus().equals(UserStatus.ACTIVE)) {
            throw new UsernameNotFoundException("User is not active");
        }
            return new CustomUserDetails(user);
    }
}
