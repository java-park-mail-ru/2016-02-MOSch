package dbStuff.dataSets;
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
    private long id;

    @Column(name = "username")
    private String username;

    @Column(name = "isAdmin")
    private Boolean isAdmin;

    @Column(name = "password")
    private String password;

    //Important to Hibernate!
    public UserDataSet() {
    }

    public UserDataSet(String name) {
        this.id = -1;
        this.username = name;
        this.isAdmin = false;
    }

    public UserDataSet(UserProfile user) {
        this.id = -1;
        this.username = user.getLogin();
        this.isAdmin = user.getAdmin();
        this.password = user.getPassword();
    }

    public String getName() {
            return username;
        }

    public void setName(String name) {
            this.username = name;
        }

    public long getId() {
            return id;
        }

    public void setId(long id) {
            this.id = id;
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
