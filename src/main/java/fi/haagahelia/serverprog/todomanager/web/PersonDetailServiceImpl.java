package fi.haagahelia.serverprog.todomanager.web;

import fi.haagahelia.serverprog.todomanager.domain.Model.person.Person;
import fi.haagahelia.serverprog.todomanager.domain.Repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class PersonDetailServiceImpl implements UserDetailsService {
    private final PersonRepository repository;


    @Autowired
    public PersonDetailServiceImpl(PersonRepository userRepository) {
        this.repository = userRepository;
    }

    /**
     * This method is used to load a user by username. It is used by Spring Security to authenticate a user.
     * @param username the username identifying the user whose data is required.
     * @return a fully populated user record (never null)
     * @throws UsernameNotFoundException if the user could not be found or the user has no GrantedAuthority
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException
    {
        Person curruser = repository.findByUsername(username);
        return new org.springframework.security.core.userdetails.User(
                username, curruser.getPassword(), AuthorityUtils.createAuthorityList(curruser.getRole()));
    }
}
