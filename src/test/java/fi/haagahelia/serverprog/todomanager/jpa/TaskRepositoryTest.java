package fi.haagahelia.serverprog.todomanager.jpa;


import fi.haagahelia.serverprog.todomanager.domain.Model.tasks.Task;
import fi.haagahelia.serverprog.todomanager.domain.Model.tasks.TaskPriority;
import fi.haagahelia.serverprog.todomanager.domain.Model.tasks.TaskStatus;
import fi.haagahelia.serverprog.todomanager.domain.Repository.PersonRepository;
import fi.haagahelia.serverprog.todomanager.domain.Repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Testing that the tasks' repository is working properly
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class TaskRepositoryTest {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private PersonRepository pRepository;


    @Test
    public void findByOwnerUsernameTest() {
        List<Task> tasksFinded = taskRepository.findByOwnerUsername("admin");
        assertThat(tasksFinded.size()).isGreaterThan(0);
        assertThat(tasksFinded.get(0).getOwner().getUsername()).isEqualTo("admin");
    }

    @Test
    public void findByStatusTest() {
        List<Task> tasksFinded = taskRepository.findByStatus(TaskStatus.DONE);
        assertThat(tasksFinded.size()).isGreaterThan(0);
        assertThat(tasksFinded.get(0).getStatus()).isEqualTo(TaskStatus.DONE);
    }

    @Test
    public void createTaskTest() {
        Task task = new Task("Server Programming", "Creating programming project", TaskStatus.DONE,
                TaskPriority.LOW, LocalDate.now(), null, pRepository.findByUsername("admin"));
        taskRepository.save(task);
        assertThat(task.getId()).isNotNull();
        assertThat(task.getTitle()).isEqualTo("Server Programming");
        assertThat(task.getDescription()).isEqualTo("Creating programming project");
        assertThat(task.getStatus()).isEqualTo(TaskStatus.DONE);
        assertThat(task.getPriority()).isEqualTo(TaskPriority.LOW);
        assertThat(task.getOwner().getUsername()).isEqualTo("admin");
    }

    @Test
    public void deleteTaskTest() {
        Task task = new Task("Server Programming", "Creating programming project", TaskStatus.DONE,
                TaskPriority.LOW, LocalDate.now(), null, pRepository.findByUsername("admin"));
        taskRepository.delete(task);
        assertThat(taskRepository.findById(task.getId())).isNull();
    }

    @Test
    public void updateTaskTest() {
        Task task = new Task("Server Programming", "Creating programming project", TaskStatus.DONE,
                TaskPriority.LOW, LocalDate.now(), null, pRepository.findByUsername("admin"));
        taskRepository.save(task);
        task.setTitle("Server Programming 2");
        task.setDescription("Creating programming project 2");
        task.setStatus(TaskStatus.IN_PROGRESS);
        task.setPriority(TaskPriority.HIGH);
        taskRepository.save(task);
        assertThat(taskRepository.findById(task.getId()).getTitle()).isEqualTo("Server Programming 2");
        assertThat(taskRepository.findById(task.getId()).getDescription()).isEqualTo("Creating programming project 2");
        assertThat(taskRepository.findById(task.getId()).getStatus()).isEqualTo(TaskStatus.IN_PROGRESS);
        assertThat(taskRepository.findById(task.getId()).getPriority()).isEqualTo(TaskPriority.HIGH);
    }
}
