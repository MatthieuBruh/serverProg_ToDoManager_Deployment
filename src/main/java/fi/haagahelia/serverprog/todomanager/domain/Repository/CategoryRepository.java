package fi.haagahelia.serverprog.todomanager.domain.Repository;

import fi.haagahelia.serverprog.todomanager.domain.Model.category.Category;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CategoryRepository<T> extends CrudRepository<Category, String> {
    Category findCategoryByTitleAndCreatorUsername(String title, String username);
    List<Category> findCategoryByCreatorUsername(String username);
}
