package org.gerimedica.exercise.service.mapper;

import org.gerimedica.exercise.service.dto.CsvEntityDto;

public class CsvMapper {

    public static CsvEntityDto rowRecordToDto(String[] row) {
        CsvEntityDto dto = new CsvEntityDto();
        dto.setSource(isEmpty(row[0]));
        dto.setCodeListCode(isEmpty(row[1]));
        dto.setCode(isEmpty(row[2]));
        dto.setDisplayValue(isEmpty(row[3]));
        dto.setLongDescription(isEmpty(row[4]));
        dto.setFromDate(isEmpty(row[5]));
        dto.setToDate(isEmpty(row[6]));
        if (!row[7].isEmpty())
            dto.setSortingPriority(Integer.valueOf(row[7]));
        return dto;
    }

    public static String isEmpty(String value) {
        return !value.isEmpty() ? value : null;
    }

}
