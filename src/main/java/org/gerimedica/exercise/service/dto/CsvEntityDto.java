package org.gerimedica.exercise.service.dto;

import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Setter
@Getter
@ToString
public class CsvEntityDto {

    // `id` is not required however, could provide it.
//    private Long id;
    private String source;
    private String codeListCode;
    @NotNull
    private String code;
    private String displayValue;
    private String longDescription;
    private String fromDate;
    private String toDate;
    private Integer sortingPriority;
}
