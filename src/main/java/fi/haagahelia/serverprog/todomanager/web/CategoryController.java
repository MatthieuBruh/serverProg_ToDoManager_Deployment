package fi.haagahelia.serverprog.todomanager.web;

import fi.haagahelia.serverprog.todomanager.domain.Model.category.Category;
import fi.haagahelia.serverprog.todomanager.domain.Model.person.Person;
import fi.haagahelia.serverprog.todomanager.domain.Model.tasks.Task;
import fi.haagahelia.serverprog.todomanager.domain.Repository.CategoryRepository;
import fi.haagahelia.serverprog.todomanager.domain.Repository.PersonRepository;
import fi.haagahelia.serverprog.todomanager.domain.Repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;


@Controller
public class CategoryController {

    @Autowired
    private CategoryRepository crepository;

    @Autowired
    private PersonRepository prepository;

    @Autowired
    private TaskRepository trepository;

    /**
     * Get all the categories of a person
     * @param request this is the http request
     * @param model this is the thymeleaf model
     * @return it returns the html file to be rendered
     */
    @RequestMapping(value = "/categories", method = RequestMethod.GET)
    public String getCategories(HttpServletRequest request, Model model) {
        Person person = prepository.findByUsername(request.getUserPrincipal().getName());
        model.addAttribute("categories", crepository.findCategoryByCreatorUsername(person.getUsername()));
        model.addAttribute("username", request.getUserPrincipal().getName());
        return "category/categories";
    }

    /**
     * This method can allow user to create a new category (this is the form model)
     * @param model this is the thymeleaf model
     * @return it returns the html file to be rendered
     */
    @RequestMapping(value = "/categories/add", method = RequestMethod.GET)
    public String addCategory(Model model) {
        model.addAttribute("category", new Category());
        return "category/addCategory";
    }

    /**
     * This method can allow user to save a new category.
     * It create a new category and save it to the database
     * @param request this is the http request
     * @param category this is the category object
     * @return it returns the html file to be rendered
     */
    @RequestMapping(value = "/categories/save", method = RequestMethod.POST)
    public String saveCategory(HttpServletRequest request, Category category) {
        Person person = prepository.findByUsername(request.getUserPrincipal().getName());
        category.setCreator(person);
        crepository.save(category);
        return "redirect:/categories";
    }

    /**
     * This method can allow user to delete a category by the id and the username of the creator
     * @param title this is the title of the category
     * @param request this is the http request
     * @return it returns the html file to be rendered
     */
    @RequestMapping(value = "/categories/delete/{title}", method = RequestMethod.GET)
    public String deleteCategory(@PathVariable("title") String title, HttpServletRequest request) {
        String username = request.getUserPrincipal().getName();
        Category category = crepository.findCategoryByTitleAndCreatorUsername(title, username);
        for (Task task : trepository.findAll()) {
            if (task.getCategory() != null) {
                if (task.getCategory().equals(category)) {
                    task.setCategory(null);
                    trepository.save(task);
                }
            }
        }
        crepository.delete(category);
        return "redirect:/categories";
    }
}
