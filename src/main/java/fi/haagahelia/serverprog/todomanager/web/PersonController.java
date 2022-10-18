package fi.haagahelia.serverprog.todomanager.web;

import fi.haagahelia.serverprog.todomanager.domain.Model.person.Person;
import fi.haagahelia.serverprog.todomanager.domain.Model.person.SignUpForm;
import fi.haagahelia.serverprog.todomanager.domain.Repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;

@Controller
public class PersonController {

    @Autowired
    private PersonRepository pRepository;

    @RequestMapping(value="/login")
    public String login() {
        return "login";
    }

    @RequestMapping(value = "/register")
    public String addStudent(Model model) {
        model.addAttribute("signupform", new SignUpForm());
        return "signup";
    }

    @RequestMapping(value = "/saveuser", method = RequestMethod.POST)
    private String save(@Valid @ModelAttribute("signupform") SignUpForm signUpForm, BindingResult bindingResult) {
        if (!bindingResult.hasErrors()) { // validation errors
            if (signUpForm.getPassword().equals(signUpForm.getPasswordCheck())) {
                Person newPerson = new Person();

                String password = signUpForm.getPassword();
                String hashedPwd = new BCryptPasswordEncoder().encode(password);

                newPerson.setUsername(signUpForm.getUsername());
                newPerson.setEmail(signUpForm.getEmail());
                newPerson.setFirstName(signUpForm.getFirstName());
                newPerson.setLastName(signUpForm.getLastName());
                newPerson.setPassword(hashedPwd);
                newPerson.setRole("USER");
                if (pRepository.findByEmail(signUpForm.getEmail()) == null) {
                    if (pRepository.findByUsername(signUpForm.getUsername()) == null) {
                        pRepository.save(newPerson);
                    } else {
                        bindingResult.rejectValue("username", "err.username", "Username already exists");
                        return "signup";
                    }
                }
                else {
                    bindingResult.rejectValue("email", "err.email", "Email already exists");
                    return "signup";
                }
            }
            else {
                bindingResult.rejectValue("passwordCheck", "err.passCheck", "Passwords does not match");
                return "signup";
            }
        }
        else {
            return "signup";
        }
        return "redirect:/login";
    }
}
