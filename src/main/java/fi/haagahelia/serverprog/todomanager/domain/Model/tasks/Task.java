package fi.haagahelia.serverprog.todomanager.domain.Model.tasks;

import fi.haagahelia.serverprog.todomanager.domain.Model.category.Category;
import fi.haagahelia.serverprog.todomanager.domain.Model.person.Person;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"title", "owner_username"})
})
public class Task implements Comparable<Task> {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @Column(nullable = false)
    private String title;
    private String description;
    @Column(nullable = false)
    private TaskStatus status;
    @Column(nullable = false)
    private TaskPriority priority;
    private LocalDate dueDate;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "owner_username", nullable = false)
    private Person owner;

    @ManyToMany(cascade = {CascadeType.REFRESH, CascadeType.MERGE}, fetch = FetchType.LAZY)
    private List<Person> participants;


    public Task() {
    }

    public Task(String title, String description, TaskStatus status, TaskPriority priority, LocalDate dueDate, Category category, Person owner) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.priority = priority;
        this.dueDate = dueDate;
        this.category = category;
        this.owner = owner;
        this.participants = new ArrayList<>();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(title, task.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title);
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", priority=" + priority +
                ", dueDate=" + dueDate +
                '}';
    }

    @Override
    public int compareTo(Task o) {
        if (this.priority.compareTo(o.priority) == 0) {
            return 0;
        } else if (this.priority.compareTo(o.priority) < 0) {
            return -1;
        } else {
            return 1;
        }
    }

    public int compareToDateAndPriority(Task o) {
        if (this.dueDate.compareTo(o.dueDate) == 0) {
            return this.compareTo(o);
        } else if (this.dueDate.compareTo(o.dueDate) < 0) {
            return 1;
        } else {
            return -1;
        }
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public TaskPriority getPriority() {
        return priority;
    }

    public void setPriority(TaskPriority priority) {
        this.priority = priority;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Person getOwner() {
        return owner;
    }

    public void setOwner(Person owner) {
        this.owner = owner;
    }

    public List<Person> getParticipants() {
        return participants;
    }

    public void setParticipants(List<Person> participants) {
        this.participants = participants;
    }

    public void addParticipant(Person participant) {
        this.participants.add(participant);
    }

    public void removeParticipant(Person participant) {
        if (participants.contains(participant)) this.participants.remove(participant);
    }
}
