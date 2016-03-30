package dbStuff.dataSets;

/**
 * Created by Olerdrive on 30.03.16.
 */
import org.jetbrains.annotations.NotNull;
import rest.UserProfile;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "scores")
public class ScoreDataSet implements Serializable { // Serializable Important to Hibernate!
    private static final long serialVersionUID = -8706689714326132798L;

    @Id
    @JoinColumn (name = "id")
    private Long id;

    @Column (name = "level", nullable = false)
    private Long level;

    @Column (name = "rating", nullable = false)
    private Long rating;

    @Column (name = "info", nullable = false)
    private String info;

    @OneToOne
    @PrimaryKeyJoinColumn(name = "id")
    private UserDataSet user;

    public ScoreDataSet() {
    }

    public ScoreDataSet(@NotNull Long id){
        this.id = id;
        this.level = 0L;
        this.rating = 0L;
        this.info = "";


    }

    public void setlevel(@NotNull Long level) {
        this.level = level;
    }

    @NotNull
    public long getLevel() { return this.level; }

    @NotNull
    public long getId() {
        return id;
    }


    public void setId(@NotNull long id) {
        this.id = id;
    }


    @NotNull
    public String getInfo() {
        return info;
    }

    public void setInfo(@NotNull String info) {
        this.info = info;
    }

    @NotNull
    public long getRating(){ return rating; }

    public void setRating(@NotNull long rating) { this.rating = rating; }



}