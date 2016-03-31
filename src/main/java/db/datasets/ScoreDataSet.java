package db.datasets;

import org.jetbrains.annotations.NotNull;

import javax.persistence.*;
import java.io.Serializable;

/**
 * MOSch-team test server for "Kill The Birds" game
 */
@SuppressWarnings("unused")
@Entity
@Table(name = "scores")
public class ScoreDataSet implements Serializable { // Serializable Important to Hibernate!
    private static final long serialVersionUID = -8706689714326132798L;

    @Id
    @JoinColumn(name = "id")
    private Long id;

    @Column(name = "level", nullable = false)
    private Long level;

    @Column(name = "rating", nullable = false)
    private Long rating;

    @Column(name = "info", nullable = false)
    private String info;

    @OneToOne
    @PrimaryKeyJoinColumn(name = "id")
    private UserDataSet user;

    public ScoreDataSet() {
    }

    public ScoreDataSet(@NotNull Long id) {
        this.id = id;
        this.level = 0L;
        this.rating = 0L;
        this.info = "";
    }

    public void setlevel(@NotNull Long level) {
        this.level = level;
    }

    @NotNull
    public Long getLevel() {
        return this.level;
    }

    @NotNull
    public Long getId() {
        return id;
    }


    public void setId(@NotNull Long id) {
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
    public Long getRating() {
        return rating;
    }

    public void setRating(@NotNull Long rating) {
        this.rating = rating;
    }
}
