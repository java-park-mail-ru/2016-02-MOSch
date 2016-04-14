package rest;

import db.datasets.UserDataSet;
import org.jetbrains.annotations.NotNull;

/**
 * MOSch-team test server for "Kill The Birds" game
 */
@SuppressWarnings({"unused", "ClassWithTooManyConstructors"})
public class UserProfile {
    @NotNull
    private String login = "";
    @NotNull
    private String password = "";
    @NotNull
    private Long id;
    @NotNull
    private Boolean isAdmin = false;
    @NotNull
    private RoleEnum role = RoleEnum.USER;

    @NotNull
    private Long rate = 0L;

    @NotNull
    private Long level = 0L;


    public UserProfile() {
        login = "";
        password = "";
        id = 0L;
        role = RoleEnum.USER;
        isAdmin = false;
        rate = 0L;
        level = 0L;
    }

    public UserProfile(@NotNull UserDataSet dataSet) {
        login = dataSet.getUsername();
        password = dataSet.getPassword();
        id = dataSet.getId();
        role = dataSet.getIsAdmin() ? RoleEnum.ADMIN : RoleEnum.USER;
        isAdmin = dataSet.getIsAdmin();
        rate = dataSet.getRate();
        level = dataSet.getLevel();
    }

    public UserProfile(@NotNull String login, @NotNull String password, @NotNull Long id) {
        this.login = login;
        this.password = password;
        this.id = id;
        this.role = RoleEnum.USER;
        this.isAdmin = false;
    }

    public UserProfile(@NotNull Long rate, @NotNull Long level){
        this.login = "";
        this.password = "";
        this.id = -1L;
        this.rate = rate;
        this.level = level;
    }

    public UserProfile(@NotNull String login, @NotNull String password) {
        this.login = login;
        this.password = password;
        this.id = 0L;
        this.role = RoleEnum.USER;
        this.isAdmin = false;
        this.rate = 0L;
        this.level = 0L;
    }

    public UserProfile(@NotNull String login, @NotNull String password, @NotNull Long id, @NotNull RoleEnum role) {
        this.login = login;
        this.password = password;
        this.id = id;
        this.role = role;
        this.isAdmin = false;
    }

    @NotNull
    public String getLogin() {
        return login;
    }

    public void setLogin(@NotNull String login) {
        this.login = login;
    }

    @NotNull
    public String getPassword() {
        return password;
    }

    public void setPassword(@NotNull String password) {
        this.password = password;
    }

    @NotNull
    public Long getId() {
        return id;
    }

    public void setId(@NotNull Long id) {
        this.id = id;
    }

    @NotNull
    public RoleEnum getRole() {
        return role;
    }

    public void setRole(@NotNull RoleEnum role) {
        this.role = role;
        this.isAdmin = (role == RoleEnum.ADMIN);
    }

    @NotNull
    public Boolean getIsAdmin() {
        return isAdmin;
    }
    public void setIsAdmin(@NotNull Boolean admin) {
        this.isAdmin = admin;
    }

    public void setRate(@NotNull Long rate) {this.rate = rate;}
    @NotNull
    public Long getRate(){return this.rate; }

    public void setLevel(@NotNull Long level) { this.level = level;}

    @NotNull
    public Long getLevel(){return this.level;}

    public enum RoleEnum {ADMIN, USER}
}
