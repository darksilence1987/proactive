package org.xhite.proactive.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{
    private final UserRepository userRepository;

    @Override
    public AppUser getUserById(long id) {
        AppUser user = userRepository.findById(id).orElse(null);
        if(user != null && user.getStatus() != UserStatus.ACTIVE) user = null;
        return user;
    }

    @Override
    public AppUser getUserByUsername(String name) {
        return userRepository.findByUsername(name).orElse(null);
    }

    @Override
    public void save(AppUser user) {
        if(getUserByUsername(user.getUsername())!= null){
            user.setId(getUserByUsername(user.getUsername()).getId());
        }
        userRepository.save(user);
    }

    @Override
    public void deleteUser(long id) {
        AppUser user = userRepository.findById(id).orElse(null);
        assert user != null;
        user.setStatus(UserStatus.DELETED);
        userRepository.save(user);
    }

    @Override
    public List<AppUser> getUsersByStatus(UserStatus userStatus) {
        return userRepository.findAll().stream()
                .filter(user -> user.getStatus()==userStatus).toList();
    }

    @Override
    public void changeUserStatus(long id, UserStatus userStatus) {
        AppUser user = userRepository.findById(id).orElse(null);
        assert user != null;
        user.setStatus(userStatus);
        userRepository.save(user);
    }

    @Override
    public List<AppUser> searchUsers(String query) {
        return userRepository.findByUsernameContainingIgnoreCase(query);
    }
}
