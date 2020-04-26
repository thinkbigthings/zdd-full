package org.thinkbigthings.zdd.server;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;
import org.thinkbigthings.zdd.dto.AddressDTO;
import org.thinkbigthings.zdd.dto.UserDTO;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;


@RestController
public class UserController {

    private final UserService service;

    // if there's only one constructor, can omit Autowired and Inject
    public UserController(UserService s) {
        service = s;
    }


    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value="/protected", method= RequestMethod.GET, produces= MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Page<UserDTO> getUsersProtected(@PageableDefault(page = 0, size = 10, sort = {"registrationTime"}, direction=Sort.Direction.DESC) Pageable page,
                                           @AuthenticationPrincipal User user)
    {
        System.out.println(user.getUsername());
        return Page.empty();
    }

    @RequestMapping(value="/user", method= RequestMethod.GET, produces= MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Page<UserDTO> getUsers(@PageableDefault(page = 0, size = 10, sort = {"registrationTime"}, direction=Sort.Direction.DESC) Pageable page) {

        return service.getUsers(page);
    }

    @RequestMapping(value="/user", method= RequestMethod.POST, produces= MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public UserDTO createUser(@RequestBody UserDTO newUser) {

        return service.saveNewUser(newUser);
    }

    @RequestMapping(value="/user/{username}", method= RequestMethod.PUT, produces= MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public UserDTO updateUser(@RequestBody UserDTO newUser, @PathVariable String username) {

        return service.updateUser(username, newUser);
    }

    @RequestMapping(value="/user/{username}", method= RequestMethod.GET, produces= MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public UserDTO getUser(@PathVariable String username) {

        return service.getUser(username);
    }
}