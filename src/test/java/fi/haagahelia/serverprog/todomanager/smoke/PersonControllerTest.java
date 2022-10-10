package fi.haagahelia.serverprog.todomanager.smoke;


import fi.haagahelia.serverprog.todomanager.web.PersonController;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Testing that the person's controller is initialized correctly
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class PersonControllerTest {

    @Autowired
    private PersonController personController;

    @Test
    public void contextLoads() throws Exception {
        assertThat(personController).isNotNull();
    }

}
