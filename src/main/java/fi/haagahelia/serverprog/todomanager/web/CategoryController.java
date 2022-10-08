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

    @RequestMapping(value = "/categories", method = RequestMethod.GET)
    public String getCategories(HttpServletRequest request, Model model) {
        Person person = prepository.findByUsername(request.getUserPrincipal().getName());
        model.addAttribute("categories", crepository.findCategoryByCreatorUsername(person.getUsername()));
        model.addAttribute("username", request.getUserPrincipal().getName());
        return "category/categories";
    }

    @RequestMapping(value = "/categories/add", method = RequestMethod.GET)
    public String addCategory(Model model) {
        model.addAttribute("category", new Category());
        return "category/addCategory";
    }

    @RequestMapping(value = "/categories/save", method = RequestMethod.POST)
    public String saveCategory(HttpServletRequest request, Category category) {
        Person person = prepository.findByUsername(request.getUserPrincipal().getName());
        category.setCreator(person);
        crepository.save(category);
        return "redirect:/categories";
    }

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
