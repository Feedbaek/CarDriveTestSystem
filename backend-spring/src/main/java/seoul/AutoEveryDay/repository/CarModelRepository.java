package seoul.AutoEveryDay.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import seoul.AutoEveryDay.entity.CarModel;

import java.util.List;
import java.util.Optional;

@Repository
public interface CarModelRepository extends JpaRepository<CarModel, Long> {
    Optional<CarModel> findByName(String name);

    List<CarModel> findAllByOrderById();

    boolean existsByName(String name);

    List<CarModel> findByNameContaining(String search);
}
