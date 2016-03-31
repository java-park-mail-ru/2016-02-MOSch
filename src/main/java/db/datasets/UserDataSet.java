package db.datasets;

import org.jetbrains.annotations.NotNull;
import rest.UserProfile;

import javax.persistence.*;
import java.io.Serializable;

/**
 * MOSch-team test server for "Kill The Birds" game
 */
@SuppressWarnings("unused")
@Entity
@Table(name = "users")
public class UserDataSet implements Serializable { // Serializable Important to Hibernate!
    private static final long serialVersionUID = -8706689714326132798L;

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", unique = true, nullable = false)
    private String login;

    @Column(name = "isAdmin", nullable = false)
    private Boolean isAdmin;

    @Column(name = "password", nullable = false)
    private String password;

    @OneToOne
    @PrimaryKeyJoinColumn(name = "id")
    private ScoreDataSet scores;

    @OneToOne
    @PrimaryKeyJoinColumn(name = "id")
    private AuthDataSet auth;

    //Important to Hibernate!
    public UserDataSet() {
    }

    public UserDataSet(@NotNull String username) {
        this.id = (long) -1;
        this.login = username;
        this.isAdmin = false;
    }

    public UserDataSet(@NotNull String username, @NotNull String password) {
        this.id = (long) -1;
        this.login = username;
        this.password = password;
        this.isAdmin = false;
    }

    public UserDataSet(@NotNull UserProfile user) {
        this.id = (long) -1;
        this.login = user.getLogin();
        this.isAdmin = user.getIsAdmin();
        this.password = user.getPassword();
    }

    @NotNull
    public Long getId() {
        return id;
    }

    public void setId(@NotNull Long id) {
        this.id = id;
    }

    @NotNull
    public String getLogin() {
        return login;
    }

    public void setLogin(@NotNull String username) {
        this.login = username;
    }

    @NotNull
    public String getPassword() {
        return password;
    }

    public void setPassword(@NotNull String password) {
        this.password = password;
    }

    @NotNull
    public Boolean getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(@NotNull Boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    @Override
    public String toString() {
        return "UserDataSet{" +
                "id=" + id +
                ", name='" + login + '\'' +
                ", is_Admin=" + isAdmin +
                '}';
    }
}
