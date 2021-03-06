package pl.databucket.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthReqDTO {

    @ApiModelProperty(position = 1, required = true, example = "username")
    private String username;

    @ApiModelProperty(position = 2, required = true, example = "example password")
    private String password;

    @ApiModelProperty(position = 3, example = "1")
    private Integer projectId;

}
