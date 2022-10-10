package fi.haagahelia.serverprog.todomanager.domain.Repository;

import fi.haagahelia.serverprog.todomanager.domain.Model.tasks.Task;
import fi.haagahelia.serverprog.todomanager.domain.Model.tasks.TaskStatus;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.List;

public interface TaskRepository extends CrudRepository<Task, Long> {
    Task findById(long id);
    List<Task> findByOwnerUsername(String username);
    List<Task> findByStatus(TaskStatus status);
}
