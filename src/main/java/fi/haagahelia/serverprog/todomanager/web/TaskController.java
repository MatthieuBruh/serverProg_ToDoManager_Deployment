package fi.haagahelia.serverprog.todomanager.web;


import fi.haagahelia.serverprog.todomanager.domain.Model.person.Person;
import fi.haagahelia.serverprog.todomanager.domain.Model.tasks.SortByDueDateAndPriority;
import fi.haagahelia.serverprog.todomanager.domain.Model.tasks.Task;
import fi.haagahelia.serverprog.todomanager.domain.Model.tasks.TaskStatus;
import fi.haagahelia.serverprog.todomanager.domain.Repository.CategoryRepository;
import fi.haagahelia.serverprog.todomanager.domain.Repository.PersonRepository;
import fi.haagahelia.serverprog.todomanager.domain.Repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.websocket.server.PathParam;
import java.security.Principal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Controller
public class TaskController {

    @Autowired
    private TaskRepository trepository;
    @Autowired
    private PersonRepository prepository;
    @Autowired
    private CategoryRepository crepository;


    private Person getPerson(Principal user) {
        if (user == null) {
            return null;
        }
        return prepository.findByUsername(user.getName());
    }

    private List<Task> extractPersonTasks(List<Task> tasks, Principal username) {
        List<Task> tasksExtracted = new ArrayList<>();
        Person person = getPerson(username);
        if (person == null) return tasksExtracted;

        for (Task task : tasks) {
            if (task.getOwner().equals(person)) {
                tasksExtracted.add(task);
            } else if (task.getParticipants().contains(person)) {
                tasksExtracted.add(task);
            }
        }
        return tasksExtracted;
    }

    @RequestMapping(value = "/home", method = RequestMethod.GET)
    private String getHomePage(HttpServletRequest request, Model model) {
        model.addAttribute("allTasks", trepository.count());
        model.addAttribute("progressTasks", trepository.findByStatus(TaskStatus.IN_PROGRESS).size());
        model.addAttribute("finishedTasks", trepository.findByStatus(TaskStatus.DONE).size());
        model.addAttribute("username", getPerson(request.getUserPrincipal()));
        return "home";
    }

    /**
     * This method is used to extract all the tasks from a user and sort them for the next 7 days.
     * @param sourceTasks List of all the tasks of a user
     * @param startDate Start date of the period
     * @return Pair of a list of dates and a hashmap of dates (key) and the number of tasks on that date (value)
     */
    private Pair<List<String>, HashMap<String, List<Task>>> getTaskNextSevenDays(List<Task> sourceTasks, LocalDate startDate) {
        List<String> dates = new ArrayList<>();
        HashMap<String, List<Task>> dateAndTask = new HashMap<>();
        for (int i = 0; i < 7; i++) {
            LocalDate date = startDate.plusDays(i);
            String dateString = date.getYear() + "-" + date.getMonthValue() + "-" + date.getDayOfMonth();
            List<Task> dayTask = new ArrayList<>();
            for (Task task : sourceTasks) {
                if (task.getDueDate().equals(date)) {
                    dayTask.add(task);
                }
            }
            dates.add(dateString);
            dateAndTask.put(dateString, dayTask);
        }
        return Pair.of(dates, dateAndTask);
    }

    /**
     * This method is used to display the schdule of a user.
     * @param request HttpServletRequest to get user's information
     * @param model The model to add data for thymeleaf
     * @return the schedule page
     */
    @RequestMapping(value = "/schedule", method = RequestMethod.GET)
    private String getSchedulePage(@RequestParam(value = "date", required = false) String startDateParam, HttpServletRequest request, Model model) {
        LocalDate startDate = startDateParam == null ? LocalDate.now() : LocalDate.parse(startDateParam);
        String username = getPerson(request.getUserPrincipal()).getUsername();
        List<Task> tasks = trepository.findByOwnerUsername(username);
        Pair<List<String>, HashMap<String, List<Task>>> pair = getTaskNextSevenDays(tasks, startDate);
        model.addAttribute("dates", pair.getFirst());
        model.addAttribute("dateAndTask", pair.getSecond());
        model.addAttribute("username", getPerson(request.getUserPrincipal()).getUsername());
        return "tasks/schedule";
    }

    @RequestMapping(value = "/tasks", method = RequestMethod.GET)
    private String getTasksPage(HttpServletRequest request, Model model) {
        List<Task> tasks = extractPersonTasks((List<Task>) trepository.findAll(), request.getUserPrincipal());
        tasks.sort(new SortByDueDateAndPriority());
        model.addAttribute("tasks", tasks);
        model.addAttribute("username", getPerson(request.getUserPrincipal()).getUsername());
        return "tasks/tasksList";
    }

    @RequestMapping(value = "/tasks/{id}", method = RequestMethod.GET)
    private String getTaskDetailsPage(@PathVariable("id") String id, HttpServletRequest request, Model model) {
        try {
            long taskId = Long.parseLong(id);
            if (trepository.findById(taskId) == null) {
                return "redirect:/tasks";
            }

            Task task = trepository.findById(taskId);
            model.addAttribute("task", task);
            model.addAttribute("categories", crepository.findCategoryByCreatorUsername(getPerson(request.getUserPrincipal()).getUsername()));
            List<Person> persons = (List<Person>) prepository.findAll();
            persons.remove(task.getOwner());
            persons.removeAll(task.getParticipants());
            model.addAttribute("persons", persons);
            model.addAttribute("username", getPerson(request.getUserPrincipal()).getUsername());
            return "tasks/task";
        } catch (NumberFormatException e) {
            return "redirect:/tasks";
        }
    }

    @RequestMapping(value = "/tasks/save", method = RequestMethod.POST)
    public String saveTask(HttpServletRequest request, Task task) {
        Task taskSource = trepository.findById(task.getId());
        if (taskSource != null) {
            task.setOwner(taskSource.getOwner());
            task.setParticipants(taskSource.getParticipants());
            if (task.getCategory().getId() == 0) {
                task.setCategory(null);
            }
        } else {
            Person owner = getPerson(request.getUserPrincipal());
            task.setOwner(owner);
            task.setParticipants(new ArrayList<>());
        }
        trepository.save(task);
        return "redirect:/tasks";
    }

    @RequestMapping(value = "/tasks/add", method = RequestMethod.GET)
    public String addTask(HttpServletRequest request, Model model) {
        model.addAttribute("task", new Task());
        Person person = getPerson(request.getUserPrincipal());
        model.addAttribute("categories", crepository.findCategoryByCreatorUsername(person.getUsername()));

        return "tasks/addTask";
    }

    @RequestMapping(value = "/tasks/addSave", method = RequestMethod.POST)
    public String addTaskSave(Task task, HttpServletRequest request) {
        Person person = getPerson(request.getUserPrincipal());
        task.setOwner(person);
        trepository.save(task);
        return "redirect:/tasks";
    }

    @RequestMapping(value ="/tasks/delete/{id}", method = RequestMethod.GET)
    public String deleteTask(@PathVariable("id") String id) {
        try {
            long taskId = Long.parseLong(id);
            if (trepository.findById(taskId) == null) {
                return "redirect:/tasks";
            }
            trepository.deleteById(taskId);
            return "redirect:../tasks/tasksList";
        } catch (NumberFormatException e) {
            return "redirect:../tasks/tasksList";
        }
    }
}
