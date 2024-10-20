package org.xhite.proactive.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{
    private final UserRepository userRepository;

    @Override
    public List<AppUser> getAllActiveUsers() {
        return userRepository.findAll().stream()
                .filter(user -> user.getStatus()==UserStatus.ACTIVE).toList();

    }

    @Override
    public AppUser getUserById(long id) {
        AppUser user = userRepository.findById(id).orElse(null);
        if(user != null && user.getStatus() != UserStatus.ACTIVE) user = null;
        return user;
    }
}
