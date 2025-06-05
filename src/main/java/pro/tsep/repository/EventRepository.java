package pro.tsep.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pro.tsep.entity.Event;
@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
}
