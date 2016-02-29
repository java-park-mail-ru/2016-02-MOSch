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

    public UserProfile() {
        login = "";
        password = "";
        id = 0L;
    }

    public UserProfile(@NotNull String login, @NotNull String password, @NotNull Long id) {
        this.login = login;
        this.password = password;
        this.id = id;
    }

    public UserProfile(@NotNull String login, @NotNull String password) {
        this.login = login;
        this.password = password;
        this.id = 0L;
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
}
