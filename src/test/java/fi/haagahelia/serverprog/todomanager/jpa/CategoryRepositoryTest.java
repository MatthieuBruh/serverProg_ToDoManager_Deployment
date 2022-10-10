package fi.haagahelia.serverprog.todomanager.jpa;


import fi.haagahelia.serverprog.todomanager.domain.Model.category.Category;
import fi.haagahelia.serverprog.todomanager.domain.Repository.CategoryRepository;
import fi.haagahelia.serverprog.todomanager.domain.Repository.PersonRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Testing that the categories' repository is working properly
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository cRepository;

    @Autowired
    private PersonRepository pRespository;

    @Test
    public void findByTitleAndCreatorUsernameShouldReturnCategory() {
        assertThat(cRepository.findCategoryByTitleAndCreatorUsername("TESTING", "admin")).isNotNull();
    }

    @Test
    public void findByCreatorUsernameShouldReturnCategories() {
        List<Category> categoryList = cRepository.findCategoryByCreatorUsername("admin");
        assertThat(categoryList).isNotNull();
        assertThat(categoryList.size()).isGreaterThan(0);
    }

    @Test
    public void createNewCategory() {
        Category category = new Category("CategoryTest", "This is category test", pRespository.findByUsername("admin"));
        cRepository.save(category);
        assertThat(category.getId()).isNotNull();
        assertThat(category.getTitle()).isEqualTo("CategoryTest");
        assertThat(category.getDescription()).isEqualTo("This is category test");
        assertThat(category.getCreator().getUsername()).isEqualTo("admin");
    }

    @Test
    public void deleteCategory() {
        Category category = new Category("CategoryTest2", "This is category test", pRespository.findByUsername("admin"));
        cRepository.save(category);
        Category categoryFinded = cRepository.findCategoryByTitleAndCreatorUsername("CategoryTest2", "admin");
        cRepository.delete(categoryFinded);
        assertThat(cRepository.findCategoryByTitleAndCreatorUsername("CategoryTest2", "admin")).isNull();
    }
}
