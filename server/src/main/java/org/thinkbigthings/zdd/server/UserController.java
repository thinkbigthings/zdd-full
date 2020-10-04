package org.thinkbigthings.zdd.server;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.web.bind.annotation.*;
import org.thinkbigthings.zdd.dto.PersonalInfo;
import org.thinkbigthings.zdd.dto.RegistrationRequest;
import org.thinkbigthings.zdd.dto.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
public class UserController {

    private final UserService userService;
    private PersistentTokenBasedRememberMeServices rememberMeServices;

    // if there's only one constructor, can omit Autowired and Inject
    public UserController(UserService userService, PersistentTokenBasedRememberMeServices rememberMeServices) {
        this.userService = userService;
        this.rememberMeServices = rememberMeServices;
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value="/user/login", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public void login(HttpServletRequest request, HttpServletResponse response) {

        // placeholder to retrieve an initial token
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value="/user/logout", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public void logout(HttpServletRequest request, HttpServletResponse response,  Authentication authentication) {

        // TODO this logs out the current user, not the specified user.
        // clears tokens from the repository and clears the cookie
        rememberMeServices.logout(request, response, authentication);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value="/user", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Page<User> getUsers(@PageableDefault(page = 0, size = 10, sort = {"registrationTime"}, direction=Sort.Direction.DESC) Pageable page) {

        return userService.getUsers(page);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value="/registration", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public User createUserRegistration(@RequestBody RegistrationRequest newUser) {

        return userService.saveNewUser(newUser);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') || #username == authentication.name")
    @RequestMapping(value="/user/{username}/personalInfo", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public User updateUser(@RequestBody PersonalInfo userData, @PathVariable String username) {

        return userService.updateUser(username, userData);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') || #username == authentication.name")
    @RequestMapping(value="/user/{username}/password/update", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public void updatePassword(@RequestBody String newPassword, @PathVariable String username, @AuthenticationPrincipal UserDetails user) {

        userService.updatePassword(username, newPassword);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') || #username == authentication.name")
    @RequestMapping(value="/user/{username}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public User getUser(@PathVariable String username) {

        return userService.getUser(username);
    }

}