package pl.databucket.dto;

import lombok.Getter;
import lombok.Setter;
import pl.databucket.configuration.Constants;

import javax.validation.constraints.Size;
import java.util.Date;
import java.util.Set;

@Getter
@Setter
public class ManageProjectDto {

    private Integer id;
    @Size(min = Constants.NAME_MIN, max = Constants.NAME_MAX)
    private String name;
    @Size(max = Constants.DESCRIPTION_MAX)
    private String description;
    private boolean enabled;
    private Date expirationDate;
    private Set<Long> usersIds;

    private String createdBy;
    private Date createdAt;
    private String modifiedBy;
    private Date modifiedAt;

}
