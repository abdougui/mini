package mini.repository;

import mini.model.User;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserRepository extends JpaRepository<User, String> {
    User findByUsername(String id);
    User findByEmail(String email);
}
