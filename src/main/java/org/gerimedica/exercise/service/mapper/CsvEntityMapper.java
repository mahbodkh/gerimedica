package org.gerimedica.exercise.service.mapper;

import org.gerimedica.exercise.domain.CsvEntity;
import org.gerimedica.exercise.service.dto.CsvEntityDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CsvEntityMapper {

    @Mapping(target = "fromDate", dateFormat = "dd-MM-yyyy")
    @Mapping(target = "toDate", dateFormat = "dd-MM-yyyy")
    CsvEntityDto toDto(CsvEntity entity);

    List<CsvEntityDto> toDto(List<CsvEntity> entities);

    @Mapping(target = "fromDate", dateFormat = "dd-MM-yyyy")
    @Mapping(target = "toDate", dateFormat = "dd-MM-yyyy")
    CsvEntity toEntity(CsvEntityDto dto);

    List<CsvEntity> toEntity(List<CsvEntityDto> dtos);

}
