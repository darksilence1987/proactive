package org.xhite.proactive.user;

import java.util.List;
import java.util.Optional;

public interface UserService {
    AppUser getUserById(long id);
    AppUser getUserByUsername(String name);

    void save(AppUser user);

    void deleteUser(long id);

    List<AppUser> getUsersByStatus(UserStatus userStatus);

    void changeUserStatus(long id, UserStatus userStatus);
}
