package projekt.zespolowy.zero_waste.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import projekt.zespolowy.zero_waste.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.username LIKE %?1% OR u.firstName LIKE %?1% OR u.lastName LIKE %?1% ORDER BY u.totalPoints DESC")
    Page<User> findBySearchAndSortByPoints(String search, Pageable pageable);

    @Query("SELECT u FROM User u WHERE u.username LIKE %?1% ORDER BY u.username ASC")
    Page<User> findBySearchAndSortByUsername(String search, Pageable pageable);

    @Query("SELECT u FROM User u WHERE u.firstName LIKE %?1% OR u.lastName LIKE %?1% ORDER BY u.firstName ASC, u.lastName ASC")
    Page<User> findBySearchAndSortByName(String search, Pageable pageable);

    @Query("SELECT u FROM User u ORDER BY u.totalPoints DESC")
    Page<User> findBySortByPoints(Pageable pageable);

    @Query("SELECT u FROM User u ORDER BY u.username ASC")
    Page<User> findBySortByUsername(Pageable pageable);

    @Query("SELECT u FROM User u ORDER BY u.firstName ASC, u.lastName ASC")
    Page<User> findBySortByName(Pageable pageable);
}
