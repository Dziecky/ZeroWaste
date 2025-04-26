package projekt.zespolowy.zero_waste.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import projekt.zespolowy.zero_waste.entity.Refund;
import projekt.zespolowy.zero_waste.entity.User;

public interface RefundRepository extends JpaRepository<Refund, Long> {

    Page<Refund> findAllByOrder_User(User user, Pageable pageable);

    @Query(
            value = "SELECT r FROM Refund r JOIN FETCH r.order o JOIN FETCH o.user",
            countQuery = "SELECT count(r) FROM Refund r"
    )
    Page<Refund> findAllWithOrderAndUser(Pageable pageable);
}
