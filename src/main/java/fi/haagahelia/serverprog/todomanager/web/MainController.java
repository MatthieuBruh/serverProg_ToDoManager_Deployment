package fi.haagahelia.serverprog.todomanager.web;


import fi.haagahelia.serverprog.todomanager.domain.Model.tasks.TaskStatus;
import fi.haagahelia.serverprog.todomanager.domain.Repository.PersonRepository;
import fi.haagahelia.serverprog.todomanager.domain.Repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

@Controller
public class MainController {

    @Autowired
    private TaskRepository trepository;

    @Autowired
    private PersonRepository prepository;

    /**
     * This methods is used to show the keys number of website. It is used on the home page.
     * @param request is the request that is made.
     * @param model is the model that is used.
     * @return the home page.
     */
    @RequestMapping(value = "/home", method = RequestMethod.GET)
    public String getHomePage(HttpServletRequest request, Model model) {
        model.addAttribute("allTasks", trepository.count());
        model.addAttribute("progressTasks", trepository.findByStatus(TaskStatus.IN_PROGRESS).size());
        model.addAttribute("finishedTasks", trepository.findByStatus(TaskStatus.DONE).size());
        if (request.getUserPrincipal() != null) {
            model.addAttribute("user", prepository.findByUsername(request.getUserPrincipal().getName()));
        }
        // model.addAttribute("user", prepository.findByUsername(request.getUserPrincipal().getName()));
        return "home";
    }
}
