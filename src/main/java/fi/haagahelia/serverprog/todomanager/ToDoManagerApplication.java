package fi.haagahelia.serverprog.todomanager;

import fi.haagahelia.serverprog.todomanager.domain.Model.category.Category;
import fi.haagahelia.serverprog.todomanager.domain.Model.person.Person;
import fi.haagahelia.serverprog.todomanager.domain.Model.tasks.Task;
import fi.haagahelia.serverprog.todomanager.domain.Model.tasks.TaskPriority;
import fi.haagahelia.serverprog.todomanager.domain.Model.tasks.TaskStatus;
import fi.haagahelia.serverprog.todomanager.domain.Repository.CategoryRepository;
import fi.haagahelia.serverprog.todomanager.domain.Repository.PersonRepository;
import fi.haagahelia.serverprog.todomanager.domain.Repository.TaskRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;

@SpringBootApplication
public class ToDoManagerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ToDoManagerApplication.class, args);
	}

	@Bean
	public CommandLineRunner demo(PersonRepository pRepository, CategoryRepository cRepository, TaskRepository tRepository) {
		return args -> {
			Person admin = new Person("admin", "admin@todo.com", "Administrator",
					"Admiral", new BCryptPasswordEncoder().encode("admin"), "ADMIN");
			pRepository.save(admin);

			Person userTest = new Person("testUser", "test@todo.com", "Userr", "Tester",
					new BCryptPasswordEncoder().encode("user"), "ADMIN");
			pRepository.save(userTest);

			Person userTest2 = new Person("testUser2", "test2@todo.com", "Userr2", "Tester2",
					new BCryptPasswordEncoder().encode("user"), "USER");
			pRepository.save(userTest2);

			Category testCat = new Category("TESTING", "This category is for testing", admin);
			cRepository.save(testCat);

			Category testCat2 = new Category("TESTING2", "This category is for testing2", admin);
			cRepository.save(testCat2);

			Task testTask = new Task("Test task", "This is the test task", TaskStatus.IN_PROGRESS, TaskPriority.HIGH, LocalDate.now().plusDays(2), testCat2, admin);
			testTask.addParticipant(userTest);
			testTask.addParticipant(userTest2);
			tRepository.save(testTask);

			Task task = new Task("Test task 2", "This is the test task 2", TaskStatus.DONE, TaskPriority.LOW, LocalDate.now().plusDays(1), testCat, admin);
			tRepository.save(task);

			Task task2 = new Task("Test task 3", "This is the test task 3", TaskStatus.NOT_STARTED, TaskPriority.HIGH, LocalDate.now().plusDays(2), testCat, admin);
			tRepository.save(task2);

			Task task3 = new Task("Test task 4", "This is the test task 4", TaskStatus.IN_PROGRESS, TaskPriority.HIGH, LocalDate.now().plusDays(3), testCat, admin);
			tRepository.save(task3);

			Task task4 = new Task("Test task 5", "This is the test task 5", TaskStatus.IN_PROGRESS, TaskPriority.HIGH, LocalDate.now().plusDays(4), testCat, admin);
			tRepository.save(task4);

			Task task5 = new Task("Test task 6", "This is the test task 6", TaskStatus.IN_PROGRESS, TaskPriority.MEDIUM, LocalDate.now().plusDays(5), testCat, admin);
			tRepository.save(task5);

			Task task6 = new Task("Test task 7", "This is the test task 7", TaskStatus.IN_PROGRESS, TaskPriority.HIGH, LocalDate.now().plusDays(6), testCat, admin);
			tRepository.save(task6);

			Task task7 = new Task("Test task 8", "This is the test task 8", TaskStatus.IN_PROGRESS, TaskPriority.LOW, LocalDate.now().plusDays(7), testCat, admin);
			tRepository.save(task7);

			Task task8 = new Task("Test task 9", "This is the test task 9", TaskStatus.IN_PROGRESS, TaskPriority.HIGH, LocalDate.now().plusDays(1), testCat, admin);
			tRepository.save(task8);

			Task task9 = new Task("Test task 10", "This is the test task 10", TaskStatus.IN_PROGRESS, TaskPriority.MEDIUM, LocalDate.now().plusDays(0), testCat, admin);
			tRepository.save(task9);

			Task task10 = new Task("Test task 11", "This is the test task 11", TaskStatus.IN_PROGRESS, TaskPriority.LOW, LocalDate.now().plusDays(0), testCat, admin);
			tRepository.save(task10);

			Task task11 = new Task("Test task 12", "This is the test task 12", TaskStatus.IN_PROGRESS, TaskPriority.HIGH, LocalDate.now().plusDays(0), testCat, admin);
			tRepository.save(task11);

			Task task12 = new Task("Test task 13", "This is the test task 13", TaskStatus.IN_PROGRESS, TaskPriority.MEDIUM, LocalDate.now().plusDays(0), testCat, admin);
			tRepository.save(task12);

		};
	}

}
