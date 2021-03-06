package system.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Set;

/**
 * Simple JavaBean domain object that represents a Station.
 */
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@Entity
@Table(name = "stations")
@SQLDelete(sql="UPDATE stations SET deleted = true WHERE id = ?")
@Where(clause="deleted = false")
public class Station extends BaseEntity {

    @NotBlank(message = "This field is required.")
    @Column(name = "stationname")
    private String stationName;

    @NotNull(message = "This field is required.")
    @Column(name = "latitude")
    private Float latitude;

    @NotNull(message = "This field is required.")
    @Column(name = "longitude")
    private Float longitude;

    @JsonIgnore
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "departure", cascade = CascadeType.ALL,
               orphanRemoval = true)
    private Set<RoutSection> sectionsFrom;

    @JsonIgnore
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "destination", cascade = CascadeType.ALL,
               orphanRemoval = true)
    private Set<RoutSection> sectionsTo;

    @JsonIgnore
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "startStation", cascade = CascadeType.ALL,
               orphanRemoval = true)
    private Set<Rout> routsFrom;

    @JsonIgnore
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "endStation", cascade = CascadeType.ALL,
               orphanRemoval = true)
    private Set<Rout> routsTo;

    public Station(String stationName) {
        this.stationName = stationName;
        this.latitude = 0f;
        this.longitude = 0f;
    }
}
