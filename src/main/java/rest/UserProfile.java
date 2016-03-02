package rest;

import org.jetbrains.annotations.NotNull;

/**
 * MOSch-team test server for "Kill The Birds" game
 */
public class UserProfile {
    @NotNull
    private String login;
    @NotNull
    private String password;
    @NotNull
    private Long id;

    public enum roleEnum {admin, user};

    @NotNull
    private roleEnum role;

    public UserProfile() {
        login = "";
        password = "";
        id = null;
        role = roleEnum.user;
    }

    public UserProfile(@NotNull String login, @NotNull String password, @NotNull Long id) {
        this.login = login;
        this.password = password;
        this.id = id;
        this.role = roleEnum.user;
    }

    public UserProfile(@NotNull String login, @NotNull String password) {
        this.login = login;
        this.password = password;
        this.id = 0L;
        this.role = roleEnum.user;
    }

    public UserProfile(@NotNull String login, @NotNull String password, @NotNull Long id, @NotNull roleEnum role) {
        this.login = login;
        this.password = password;
        this.id = id;
        this.role = role;
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


    @NotNull
    public Long getId() {
        return id;
    }

    public void setPassword(@NotNull String password) {
        this.password = password;
    }

    public void setId(@NotNull Long id) { this.id = id; }

    @NotNull
    public roleEnum getRole() { return role;}

    public void setRole(@NotNull roleEnum role) {this.role = role;}
}
