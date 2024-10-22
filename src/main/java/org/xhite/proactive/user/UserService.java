package org.xhite.proactive.user;

import java.util.List;

public interface UserService {
    List<AppUser> getAllActiveUsers();
    AppUser getUserById(long id);
    AppUser getUserByUsername(String name);
}
