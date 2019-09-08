package org.thinkbigthings.zdd.server;

import org.springframework.http.MediaType;
import org.springframework.http.converter.protobuf.ProtobufHttpMessageConverter;
import org.springframework.web.bind.annotation.*;
import org.thinkbigthings.zdd.dto.AddressDTO;
import org.thinkbigthings.zdd.dto.UserDTO;
import org.thinkbigthings.zdd.pb.ListUserPB;
import org.thinkbigthings.zdd.pb.UserPB;

import java.util.List;
import java.util.stream.Collectors;

import static org.thinkbigthings.zdd.dto.MediaType.APPLICATION_PROTOBUF_VALUE;


@RestController
public class UserController {

    private final UserService service;

    // if there's only one constructor, can omit Autowired and Inject
    public UserController(UserService s) {
        service = s;
    }

    @RequestMapping(value="/user", method= RequestMethod.GET, produces= MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<UserDTO> getUsers() {

        return service.getUsers();
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

    @RequestMapping(value="/user", method= RequestMethod.GET, produces=APPLICATION_PROTOBUF_VALUE)
    @ResponseBody
    public ListUserPB getUsersPb() {

        return service.getUsersPb();
    }

    @RequestMapping(value="/user/{username}", method= RequestMethod.GET, produces=APPLICATION_PROTOBUF_VALUE)
    @ResponseBody
    public UserPB getUserPb(@PathVariable String username) {

        return service.getUserPb(username);
    }

}