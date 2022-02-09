package mini.services;

import mini.model.User;

import java.util.List;

public interface UserService {
    User saveUser(User user);
    User getUser(String username);
    List<User> getUsers();
    void deleteAll();
}
