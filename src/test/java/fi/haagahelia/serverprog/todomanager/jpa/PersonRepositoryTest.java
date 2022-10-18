package fi.haagahelia.serverprog.todomanager.jpa;

import fi.haagahelia.serverprog.todomanager.domain.Model.person.Person;
import fi.haagahelia.serverprog.todomanager.domain.Repository.PersonRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Testing that the persons' repository is working properly
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class PersonRepositoryTest {

    @Autowired
    private PersonRepository pRepository;

    @Test
    public void findByUsernameTest() {
        Person person = new Person("myAdmin", "myAdmin@haaga.fi", "My", "Admin",
                new BCryptPasswordEncoder().encode("admin"), "USER");
        pRepository.save(person);

        Person personFinded = pRepository.findByUsername("myAdmin");
        assertThat(pRepository.findByUsername("admin")).isNotNull();
        assertThat(personFinded.getUsername()).isEqualTo("myAdmin");
        assertThat(personFinded.getEmail()).isEqualTo("myAdmin@haaga.fi");
        assertThat(personFinded.getFirstName()).isEqualTo("My");
        assertThat(personFinded.getLastName()).isEqualTo("Admin");
        assertThat(personFinded.getRole()).isEqualTo("USER");
    }

    @Test
    public void findByEmailTest() {
        Person personFinded = pRepository.findByEmail("managertodos@gmail.com");
        assertThat(personFinded.getUsername()).isEqualTo("admin");
        assertThat(personFinded.getEmail()).isEqualTo("managertodos@gmail.com");
        assertThat(personFinded.getFirstName()).isEqualTo("Administrator");
        assertThat(personFinded.getLastName()).isEqualTo("Admiral");
        assertThat(personFinded.getRole()).isEqualTo("ADMIN");
    }

    @Test
    public void createPersonTest() {
        Person person = new Person("java", "java@todo.com", "Java", "Programmer",
                new BCryptPasswordEncoder().encode("java"), "USER");
        pRepository.save(person);
        assertThat(pRepository.findByUsername("java")).isNotNull();
    }

    @Test
    public void deletePersonTest() {
        Person person = new Person("JavaJunior", "java.junior@todo.com", "Java", "Junior",
                new BCryptPasswordEncoder().encode("java"), "USER");
        pRepository.save(person);
        Person personFinded = pRepository.findByUsername("JavaJunior");
        pRepository.delete(personFinded);
        assertThat(pRepository.findByUsername("JavaJunior")).isNull();
    }
}
