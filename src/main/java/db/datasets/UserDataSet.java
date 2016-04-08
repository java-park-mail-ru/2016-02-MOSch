package db.datasets;

import org.jetbrains.annotations.NotNull;
import rest.UserProfile;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Blob;
import java.util.Date;

/**
 * MOSch-team test server for "Kill The Birds" game
 * Users {
 id: string
 login: string
 password: string
 level: int
 rate: int
 auth_token: string
 expires: date
 info: text
 }
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
    private String username;

    @Column(name = "isAdmin", nullable = false)
    private Boolean isAdmin;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "rate", nullable = false)
    private Long rate;

    @Column(name = "level", nullable = false)
    private Long level;

    @Column(name = "auth_token", unique = false, nullable = false)
    private String authToken;

    @SuppressWarnings("FieldCanBeLocal")
    @Column(name = "date", columnDefinition = "DATETIME", nullable = false)
    private Date date;

    @Column(name = "info", columnDefinition="TEXT", nullable = false)
    private String info;

    //Important to Hibernate!
    public UserDataSet() {
    }

    public UserDataSet(@NotNull String username) {
        this.id = (long) -1;
        this.username = username;
        this.isAdmin = false;
    }

    public UserDataSet(@NotNull String username, @NotNull String password) {
        this.id = (long) -1;
        this.username = username;
        this.password = password;
        this.isAdmin = false;
        this.rate = 0L;
        this.level = 0L;
        this.authToken = "";
        this.date = new Date();
        this.info = "";
    }

    public UserDataSet(@NotNull UserProfile user) {
        this.id = (long) -1;
        this.username = user.getLogin();
        this.isAdmin = user.getIsAdmin();
        this.password = user.getPassword();
        this.rate = user.getRate();
        this.level = user.getLevel();
        this.authToken = "";
        this.date = new Date();
        this.info = "";
    }

    @NotNull
    public Long getId() {
        return id;
    }
    public void setId(@NotNull Long id) {
        this.id = id;
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
    public Boolean getIsAdmin() {
        return isAdmin;
    }
    public void setIsAdmin(@NotNull Boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    @NotNull
    public Long getScores(){return this.rate;}
    public void setScores(@NotNull Long scores) { this.rate = scores; }

    @NotNull
    public Long getLevel(){return this.level;}
    public void setLevel(@NotNull Long level) { this.level = level; }

    @NotNull
    public String getAuthToken() {
        return this.authToken;
    }
    public void setAuthToken(@NotNull String authToken) {
        this.authToken = authToken;
    }



    @Override
    public String toString() {
        return "UserDataSet{" +
                "id=" + id +
                ", name='" + username + '\'' +
                ", is_Admin=" + isAdmin +
                '}';
    }
}
