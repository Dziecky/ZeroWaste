package projekt.zespolowy.zero_waste.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import projekt.zespolowy.zero_waste.entity.Announcement;

@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {
}