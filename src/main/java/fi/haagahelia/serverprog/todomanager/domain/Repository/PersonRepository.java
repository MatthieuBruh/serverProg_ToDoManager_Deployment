package fi.haagahelia.serverprog.todomanager.domain.Repository;

import fi.haagahelia.serverprog.todomanager.domain.Model.person.Person;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PersonRepository extends CrudRepository<Person, String> {
    Person findByUsername(String username);
    Person findByEmail(String email);
    List<Person> findAllByRole(String role);
}
