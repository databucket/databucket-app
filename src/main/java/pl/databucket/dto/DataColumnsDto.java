package pl.databucket.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class DataColumnsDto {
    private Long id;
    private String name;
    private String description;
    private Long dataClassId;
    private Long bucketId;
    private List<ColumnDto> columns;
}