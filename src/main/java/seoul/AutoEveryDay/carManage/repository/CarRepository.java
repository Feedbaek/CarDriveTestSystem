package seoul.AutoEveryDay.carManage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import seoul.AutoEveryDay.carManage.entity.Car;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {
}
