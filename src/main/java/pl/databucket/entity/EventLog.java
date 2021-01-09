package pl.databucket.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Filter;
import pl.databucket.tenant.TenantSupport;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name="event_log")
@Filter(name = "projectFilter", condition = "project_id = :projectId")
public class EventLog extends AuditableCreatedDate<String> implements TenantSupport {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "event_log_generator")
    @SequenceGenerator(name="event_log_generator", sequenceName = "event_log_seq", allocationSize = 1)
    @Column(name = "event_log_id", updatable = false, nullable = false)
	private long id;

    @Column(name = "project_id", nullable = false)
    private Integer projectId;

    @OneToOne
    @JoinColumn(name = "event_id", referencedColumnName = "event_id", nullable = false)
    private Event event;

    @OneToOne
    @JoinColumn(name = "task_id", referencedColumnName = "task_id", nullable = false)
    private Task task;

    @OneToOne
    @JoinColumn(name = "bucket_id", referencedColumnName = "bucket_id", nullable = false)
    private Bucket bucket;

    @Column(nullable = false)
    private Integer affected;

	private Boolean deleted = false;
}
