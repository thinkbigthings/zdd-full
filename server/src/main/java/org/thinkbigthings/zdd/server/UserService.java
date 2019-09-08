package org.thinkbigthings.zdd.server;

import org.springframework.stereotype.Service;
import org.thinkbigthings.zdd.dto.AddressDTO;
import org.thinkbigthings.zdd.dto.UserDTO;
import org.thinkbigthings.zdd.pb.AddressPB;
import org.thinkbigthings.zdd.pb.ListUserPB;
import org.thinkbigthings.zdd.pb.UserPB;

import javax.transaction.Transactional;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;


@Service
@Transactional
public class UserService {


    private final UserRepository userRepo;

    public UserService(UserRepository repo) {
        userRepo = repo;
    }

    public UserDTO updateUser(String username, UserDTO userDto) {

        var user = userRepo.findByUsername(username);

        user.setEmail(userDto.email);
        user.setDisplayName(userDto.displayName);
        user.setPhoneNumber(userDto.phoneNumber);
        user.setHeightCm(userDto.heightCm);

        user.getAddresses().forEach(a -> a.setUser(null));
        user.getAddresses().clear();

        List<Address> newAddressEntities = userDto.addresses.stream().map(this::fromDto).collect(toList());
        user.getAddresses().addAll(newAddressEntities);
        user.getAddresses().forEach(a -> a.setUser(user));

        return toDto(userRepo.save(user));
    }

    public UserDTO saveNewUser(UserPB userDto) {

        var user = fromPb(userDto);
        user.setRegistrationTime(Instant.now());
        user.setEnabled(true);

        return toDto(userRepo.save(user));
    }

    public UserDTO saveNewUser(UserDTO userDto) {

        var user = fromDto(userDto);
        user.setRegistrationTime(Instant.now());
        user.setEnabled(true);

        return toDto(userRepo.save(user));
    }

    public List<UserDTO> getUsers() {

        return userRepo.findRecent().stream().map(this::toDto).collect(toList());
    }

    public ListUserPB getUsersPb() {

//        return ListUserPB.newBuilder()
//                .addAllUsers(userRepo.findRecent().stream().map(this::toPb).collect(toList()))
//                .build();

        return ListUserPB.newBuilder()
                .addUsers(UserPB.newBuilder().setDisplayName("name goes here!"))
                .build();
    }

    public UserDTO getUser(String username) {

        return toDto(userRepo.findByUsername(username));
    }

    public UserPB getUserPb(String username) {

        return toPb(userRepo.findByUsername(username));
    }

    public User fromPb(UserPB userData) {

        var user = new User(userData.getUsername(), userData.getDisplayName());

        user.setEmail(userData.getEmail());
        user.setPhoneNumber(userData.getPhoneNumber());
        user.setHeightCm(userData.getHeightCm());

        userData.getAddressesList().stream()
                .map(this::fromPb)
                .peek(a -> a.setUser(user))
                .collect(Collectors.toCollection(() -> user.getAddresses()));

        return user;
    }

    public User fromDto(UserDTO userData) {

        var user = new User(userData.username, userData.displayName);

        user.setEmail(userData.email);
        user.setPhoneNumber(userData.phoneNumber);
        user.setHeightCm(userData.heightCm);

        userData.addresses.stream()
                .map(this::fromDto)
                .peek(a -> a.setUser(user))
                .collect(Collectors.toCollection(() -> user.getAddresses()));

        return user;
    }

    public UserPB toPb(User user) {

        var userData = UserPB.newBuilder()
                .setDisplayName(user.getDisplayName())
                .setEmail(user.getEmail())
                .setUsername(user.getUsername())
                .setRegistrationTime(user.getRegistrationTime().toString())
                .setPhoneNumber(user.getPhoneNumber())
                .setHeightCm(user.getHeightCm());

        user.getAddresses().stream()
                .map(this::toPb)
                .collect(collectingAndThen(toList(), userData::addAllAddresses));

        return userData.build();
    }

    public AddressPB toPb(Address address) {

        return AddressPB.newBuilder()
                .setLine1(address.getLine1())
                .setCity(address.getCity())
                .setState(address.getState())
                .setZip(address.getZip())
                .build();
    }

    public UserDTO toDto(User user) {

        var userData = new UserDTO();

        userData.displayName = user.getDisplayName();
        userData.email = user.getEmail();
        userData.username = user.getUsername();
        userData.registrationTime = user.getRegistrationTime().toString();
        userData.phoneNumber = user.getPhoneNumber();
        userData.heightCm = user.getHeightCm();

        user.getAddresses().stream()
                .map(this::toDto)
                .collect(Collectors.toCollection(() -> userData.addresses));

        return userData;
    }

    public Address fromDto(AddressDTO addressData) {

        var address = new Address();

        address.setLine1(addressData.line1);
        address.setCity(addressData.city);
        address.setState(addressData.state);
        address.setZip(addressData.zip);

        return address;
    }

    public Address fromPb(AddressPB addressData) {

        var address = new Address();

        address.setLine1(addressData.getLine1());
        address.setCity(addressData.getCity());
        address.setState(addressData.getState());
        address.setZip(addressData.getZip());

        return address;
    }

    public AddressDTO toDto(Address address) {

        var addressData = new AddressDTO();

        addressData.line1 = address.getLine1();
        addressData.city = address.getCity();
        addressData.state = address.getState();
        addressData.zip = address.getZip();

        return addressData;
    }


}
