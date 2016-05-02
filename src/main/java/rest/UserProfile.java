package rest;

import db.datasets.UserDataSet;
import org.jetbrains.annotations.NotNull;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * MOSch-team test server for "Kill The Birds" game
 */
@SuppressWarnings({"unused", "ClassWithTooManyConstructors"})
public class UserProfile {
    @NotNull
    private String username = "";
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
        username = "";
        password = "";
        id = 0L;
        role = RoleEnum.USER;
        isAdmin = false;
        rate = 0L;
        level = 0L;
    }

    public UserProfile(@NotNull UserDataSet dataSet) {
        username = dataSet.getUsername();
        password = dataSet.getPassword();
        id = dataSet.getId();
        role = dataSet.getIsAdmin() ? RoleEnum.ADMIN : RoleEnum.USER;
        isAdmin = dataSet.getIsAdmin();
        rate = dataSet.getRate();
        level = dataSet.getLevel();
    }

    public UserProfile(@NotNull String username, @NotNull String password, @NotNull Long id) {
        this.username = username;
        this.password = password;
        this.id = id;
        this.role = RoleEnum.USER;
        this.isAdmin = false;
    }

    public UserProfile(@NotNull Long rate, @NotNull Long level) {
        this.username = "";
        this.password = "";
        this.id = -1L;
        this.rate = rate;
        this.level = level;
    }

    public UserProfile(@NotNull String username, @NotNull String password) {
        this.username = username;
        this.password = password;
        this.id = 0L;
        this.role = RoleEnum.USER;
        this.isAdmin = false;
        this.rate = 0L;
        this.level = 0L;
    }

    public UserProfile(@NotNull String username, @NotNull String password, @NotNull Long id, @NotNull RoleEnum role) {
        this.username = username;
        this.password = password;
        this.id = id;
        this.role = role;
        this.isAdmin = false;
    }

    @NotNull
    public String getUsername() {
        return username;
    }

    public void setUsername(@NotNull String username) {
        this.username = username;
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

    @NotNull
    public Long getRate() {
        return this.rate;
    }

    public void setRate(@NotNull Long rate) {
        this.rate = rate;
    }

    @NotNull
    public Long getLevel() {
        return this.level;
    }

    public void setLevel(@NotNull Long level) {
        this.level = level;
    }

    public enum RoleEnum {ADMIN, USER}

    public boolean checkLogin() {
        final Pattern p = Pattern.compile("^[A-Za-z0-9]{1,32}$");
        final Matcher m = p.matcher(this.username);
        return m.matches();
    }

    public boolean checkPassword() {
        final Pattern p = Pattern.compile("^[A-Za-z0-9]{6,32}$");
        final Matcher m = p.matcher(this.password);
        return m.matches();
    }
}
