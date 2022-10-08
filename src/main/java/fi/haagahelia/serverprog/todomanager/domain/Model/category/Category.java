package fi.haagahelia.serverprog.todomanager.domain.Model.category;

import fi.haagahelia.serverprog.todomanager.domain.Model.person.Person;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"title", "creator_username"})
})
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @Column(nullable = false)
    private String title;
    private String description;
    @ManyToOne
    @JoinColumn(name = "creator_username", nullable = false)
    private Person creator;

    public Category() {
    }

    public Category(String title, String description, Person creator) {
        this.title = title;
        this.description = description;
        this.creator = creator;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return title.equals(category.title) && creator.equals(category.creator);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, creator);
    }

    @Override
    public String toString() {
        return "Category{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public Person getCreator() {
        return creator;
    }

    public void setCreator(Person creator) {
        this.creator = creator;
    }
}
