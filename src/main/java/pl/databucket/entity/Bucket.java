package pl.databucket.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;
import pl.databucket.tenant.TenantSupport;


@Entity
@Getter
@Setter
@Table(name="buckets")
@FilterDef(name = "projectFilter", parameters=@ParamDef(name="projectId", type="int"))
@Filter(name = "projectFilter", condition = "project_id = :projectId")
public class Bucket extends Auditable<String> implements TenantSupport {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "bucket_generator")
    @SequenceGenerator(name="bucket_generator", sequenceName = "bucket_seq", allocationSize = 1)
    @Column(name = "bucket_id", updatable = false, nullable = false)
    private long id;

    @Column(name = "project_id", nullable = false)
    private Integer projectId;

    @Column(name = "bucket_name", length = 50)
    private String name;

    @Column(name = "icon_name", length = 50)
    private String iconName;

    @Column
    private String description;

    @OneToOne
    @JoinColumn(name = "class_id", referencedColumnName = "class_id")
    private DataClass dataClass;

    @Column(nullable = false)
    private boolean history = false;

    @ManyToMany(mappedBy = "buckets")
    private Set<User> users;

    @JsonIgnore
    private Boolean deleted = false;

    public List<Long> getListOfUsers() {
        return users.stream().map(User::getId).collect(Collectors.toList());
    }

}