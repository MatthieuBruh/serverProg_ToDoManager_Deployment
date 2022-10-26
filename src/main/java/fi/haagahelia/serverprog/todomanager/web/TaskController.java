package fi.haagahelia.serverprog.todomanager.web;


import fi.haagahelia.serverprog.todomanager.domain.Model.EmailMessage;
import fi.haagahelia.serverprog.todomanager.domain.Model.category.Category;
import fi.haagahelia.serverprog.todomanager.domain.Model.person.Person;
import fi.haagahelia.serverprog.todomanager.domain.Model.tasks.SortByDueDateAndPriority;
import fi.haagahelia.serverprog.todomanager.domain.Model.tasks.Task;
import fi.haagahelia.serverprog.todomanager.domain.Repository.CategoryRepository;
import fi.haagahelia.serverprog.todomanager.domain.Repository.EmailService;
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

    @Autowired
    private EmailService emailService;


    /**
     * This method is used to get the username of an user.
     * @param user is the user that is logged in.
     * @return the username of the user.
     */
    public Person getPerson(Principal user) {
        if (user == null) {
            return null;
        }
        return prepository.findByUsername(user.getName());
    }

    /**
     * This method is used to extract all the task that involves the user.
     * @param tasks is the list of tasks that is extracted.
     * @param username is the username of the person.
     * @return the list of tasks.
     */
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

    /**
     * This method is used to display the tasks of a user.
     * @param request HttpServletRequest to get user's information
     * @param model The model to add data for thymeleaf
     * @return the tasks page
     */
    @RequestMapping(value = "/tasks", method = RequestMethod.GET)
    private String getTasksPage(@RequestParam(value = "category", required = false) String catName, HttpServletRequest request, Model model) {
        Category categoryFilter = catName == null || catName.equals("All") ? null : crepository.findCategoryByTitleAndCreatorUsername(catName, getPerson(request.getUserPrincipal()).getUsername());

        List<Task> tasks = extractPersonTasks((List<Task>) trepository.findAll(), request.getUserPrincipal());
        if (categoryFilter != null) {
            // System.out.println("Filtering by category: " + categoryFilter.getTitle());
            tasks.removeIf(task -> !task.getCategory().getTitle().equals(categoryFilter.getTitle()));
        }
        tasks.sort(new SortByDueDateAndPriority());
        model.addAttribute("tasks", tasks);
        model.addAttribute("username", getPerson(request.getUserPrincipal()).getUsername());
        model.addAttribute("categories", crepository.findCategoryByCreatorUsername(getPerson(request.getUserPrincipal()).getUsername()));
        return "tasks/tasksList";
    }

    /**
     * This method is used to display the details of a task.
     * @param id is the id of the task.
     * @param request is the request that is made.
     * @param model is the model that is used.
     * @return the task details page.
     */
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
            model.addAttribute("person", new Person());
            model.addAttribute("persons", persons);
            model.addAttribute("username", getPerson(request.getUserPrincipal()).getUsername());
            return "tasks/task";
        } catch (NumberFormatException e) {
            return "redirect:/tasks";
        }
    }

    /**
     * This method is used to display the form to create or modify a task.
     * @param request is the request that is made.
     * @param task is the task that is created.
     * @return the task creation page.
     */
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
            if (task.getCategory().getId() == 0) {
                task.setCategory(null);
            }
            System.out.println("Task date: " + task.getDueDate());
            System.out.println("Task date length: " + task.getDueDate().toString().length());
        }
        trepository.save(task);
        return "redirect:/tasks";
    }

    /**
     * This methods is used to add a new participants to a task.
     * @param id is the id of the task.
     * @param person is the person that is added.
     * @return the task details page.
     */
    @RequestMapping(value ="/tasks/save/addParticipant/{id}", method = RequestMethod.POST)
    public String addParticipant(@PathVariable(name = "id") String id, Person person) {
        try {
            long taskId = Long.parseLong(id);
            Task task = trepository.findById(taskId);
            Person personFinded = prepository.findByUsername(person.getUsername());
            task.addParticipant(personFinded);
            trepository.save(task);
        }catch (NumberFormatException e) {
            return "redirect:../tasks/tasksList";
        }
        return "redirect:/tasks/" + id;
    }

    /**
     * This method is used to create a new task. It is used when the user click on the button "create a new task".
     * @param request is the request that is made.
     * @param model is the model that is used.
     * @return the task creation page.
     */
    @RequestMapping(value = "/tasks/add", method = RequestMethod.GET)
    public String addTask(HttpServletRequest request, Model model) {
        model.addAttribute("task", new Task());
        Person person = getPerson(request.getUserPrincipal());
        model.addAttribute("categories", crepository.findCategoryByCreatorUsername(person.getUsername()));
        return "tasks/addTask";
    }

    /**
     * This method is used to save the form of a new task.
     * @param task is the task that is created.
     * @param request is the request that is made.
     * @return the task details page.
     */
    @RequestMapping(value = "/tasks/addSave", method = RequestMethod.POST)
    public String addTaskSave(Task task, HttpServletRequest request) {
        Person person = getPerson(request.getUserPrincipal());
        task.setOwner(person);
        trepository.save(task);
        return "redirect:/tasks";
    }

    /**
     * This method is used to delete a task by its id.
     * @param id is the id of the task.
     * @return the tasks page.
     */
    @RequestMapping(value ="/tasks/delete/{id}", method = RequestMethod.GET)
    public String deleteTask(@PathVariable("id") String id) {
        try {
            long taskId = Long.parseLong(id);
            if (trepository.findById(taskId) == null) {
                return "redirect:/tasks";
            }
            trepository.deleteById(taskId);
            return "redirect:/tasks/tasksList";
        } catch (NumberFormatException e) {
            return "redirect:/tasks/tasksList";
        }
    }

    /**
     * This method is used to notify all the participants of a task.
     * We use the class EmailMessage to create the emails details and we send it with the class EmailService.
     * @param id is the id of the task.
     * @return the task details page.
     */
    @RequestMapping(value = "/tasks/{id}/notify", method = RequestMethod.GET)
    public String notifyParticipants(@PathVariable("id") String id) {
        try {
            long taskId = Long.parseLong(id);
            if (trepository.findById(taskId) == null) {
                return "redirect:/tasks";
            }
            Task task = trepository.findById(taskId);
            List<Person> participants = task.getParticipants();
            for (Person p : participants) {
                EmailMessage em = new EmailMessage();
                em.setRecipient(p.getEmail());
                em.setSubject("Informations about the shared task" + task.getTitle());
                em.setBody("Don't forget to complete the task: " + task.getTitle()
                        + "\n" + task.getDescription() + ". \nThe due date is: " + task.getDueDate());
                emailService.sendSimpleMail(em);
            }
            return "redirect:/tasks/" + id;
        } catch (NumberFormatException e) {
            return "redirect:../tasks/tasksList";
        }
    }
}
