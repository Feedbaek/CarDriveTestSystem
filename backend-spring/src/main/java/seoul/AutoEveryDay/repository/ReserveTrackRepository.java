package seoul.AutoEveryDay.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import seoul.AutoEveryDay.entity.ReserveTrack;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReserveTrackRepository extends JpaRepository<ReserveTrack, Long> {
    Optional<ReserveTrack> findByTrack_IdAndDate(Long trackId, LocalDate time);
    Optional<ReserveTrack> findByTrack_IdAndDateAndIsCanceled(Long trackId, LocalDate time, Boolean isCanceled);

    Optional<ReserveTrack> findByUser_IdAndTrack_IdAndDate(Long userId, Long trackId, LocalDate time);

    boolean existsByTrack_IdAndDateGreaterThanEqualAndIsCanceled(Long trackId, LocalDate time, Boolean isCanceled);

    List<ReserveTrack> findByTrack_IdAndDateGreaterThanEqual(Long trackId, LocalDate date);

    List<ReserveTrack> findByUser_Id(Long userId);

    List<ReserveTrack> findByUser_IdAndTrack_NameContaining(Long id, String search);
}
