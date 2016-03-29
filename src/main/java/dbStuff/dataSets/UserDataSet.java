package dbStuff.dataSets;
import org.jetbrains.annotations.NotNull;
import rest.UserProfile;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "users")
public class UserDataSet implements Serializable { // Serializable Important to Hibernate!
    private static final long serialVersionUID = -8706689714326132798L;

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username")
    private String login;

    @Column(name = "isAdmin")
    private Boolean isAdmin;

    @Column(name = "password")
    private String password;

    //Important to Hibernate!
    public UserDataSet() {
    }

    public UserDataSet(String name) {
        this.id = (long)-1;
        this.login = name;
        this.isAdmin = false;
    }

    public UserDataSet(@NotNull String login, @NotNull String password) {
        this.id = (long)-1;
        this.login = login;
        this.password = password;
        this.isAdmin = false;
    }

    public UserDataSet(UserProfile user) {
        this.id = (long)-1;
        this.login = user.getLogin();
        this.isAdmin = user.getAdmin();
        this.password = user.getPassword();
    }


    public void setName(String name) {
            this.login = name;
        }

    @NotNull
    public long getId() {
            return id;
        }

    public void setId(@NotNull long id) {
            this.id = id;
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


    @Override
    public String toString() {
        return "UserDataSet{" +
                "id=" + id +
                ", name='" + login + '\'' +
                ", is_Admin=" + isAdmin +
                '}';
        }

}
