package org.thinkbigthings.zdd.server.entity;

import org.thinkbigthings.zdd.server.Address;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static java.util.stream.Collectors.toList;

@Entity
@Table(name = "app_user", uniqueConstraints = {@UniqueConstraint(columnNames = {"username"})})
public class User implements Serializable {

    public enum Role {
        ADMIN, USER;
    }

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, insertable = false, nullable = false)
    private Long id;

    @Column(unique=true)
    @NotNull
    @Size(min = 3, message = "must be at least three characters")
    private String username = "";

    @NotNull
    private String password = "";

    @NotNull
    @Column(unique=true)
    @Size(min = 3, message = "must be at least three characters")
    private String email = "";

    @NotNull
    @Size(min = 3, message = "must be at least three characters")
    @Column(name="display_name")
    private String displayName = "";

    @Basic
    private boolean enabled = false;

    @ElementCollection(targetClass=Role.class)
    @CollectionTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role_id")
    @Enumerated(EnumType.ORDINAL)
    private Set<Role> roles = new HashSet<>();

    @Basic
    @NotNull
    private Instant registrationTime;

    @Basic
    @NotNull
    private String phoneNumber = "";

    @Basic
    @NotNull
    private int heightCm = 0;

    @OneToMany(fetch=FetchType.EAGER, mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval=true)
    private Set<Address> addresses = new HashSet<>();

    // If we check session status on all user accesses, might as well make it eager too
    // default cascade is that no operations are cascaded
    @OneToMany(fetch=FetchType.EAGER, mappedBy = "user")
    private Set<Session> sessions = new HashSet<>();

    protected User() {

    }

    public User(String name, String display) {
        username = name;
        displayName = display;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public void addRole(Role role) {
        roles.add(role);
    }

    public String[] mapRoleNames() {
        return getRoles().stream()
                .map(Role::name)
                .collect(toList())
                .toArray(new String[]{});
    }

    public Instant getRegistrationTime() {
        return registrationTime;
    }

    public void setRegistrationTime(Instant registration) {
        this.registrationTime = registration;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public int getHeightCm() {
        return heightCm;
    }

    public void setHeightCm(int height) {
        this.heightCm = height;
    }

    public Set<Address> getAddresses() {
        return addresses;
    }

    public void setAddresses(Set<Address> addresses) {
        this.addresses = addresses;
    }

    public Set<Session> getSessions() {
        return sessions;
    }
}
