package org.gerimedica.exercise.service;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gerimedica.exercise.domain.CsvEntity;
import org.gerimedica.exercise.exception.CsvException;
import org.gerimedica.exercise.exception.CsvFileException;
import org.gerimedica.exercise.repository.CsvRepository;
import org.gerimedica.exercise.service.dto.CsvEntityDto;
import org.gerimedica.exercise.service.mapper.CsvEntityMapper;
import org.gerimedica.exercise.service.mapper.CsvMapper;
import org.gerimedica.exercise.service.util.ByteArrayInOutStream;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class CsvService {
    private final CsvRepository csvRepository;
    private final CsvEntityMapper csvEntityMapper;

    public ByteArrayInputStream fetchByCodeGetCsvFile(String code) {
        log.debug("try to fetch csv file by code: {} ", code);
        final CsvEntity csvEntity = csvRepository.findByCode(code).orElseThrow(() -> new CsvException("The code does not provided."));
        List<CsvEntityDto> csvEntityDtos = new ArrayList<>();
        csvEntityDtos.add(csvEntityMapper.toDto(csvEntity));
        try {
            return generateCsvData(csvEntityDtos);
        } catch (IOException e) {
            throw new CsvFileException("It not possible fetch by code csv data.");
        }
    }

    public void deleteAll() {
        log.debug("All data has been deleted");
        csvRepository.deleteAll();
    }


    public List<CsvEntityDto> uploadCsvFile(MultipartFile multipart) {
        final List<CsvEntityDto> rowsToDto = new ArrayList<>();
        try (Reader reader = new InputStreamReader(multipart.getInputStream())) {
            try (CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(1).build()) {
                for (String[] rowValues : csvReader) {
                    final CsvEntityDto csvEntityDto = CsvMapper.rowRecordToDto(rowValues);
                    final CsvEntity csvEntity = csvEntityMapper.toEntity(csvEntityDto);
                    if (!csvRepository.findByCode(csvEntity.getCode()).isPresent()) {
                        final CsvEntity saved = csvRepository.save(csvEntity);
                        log.debug("Csv row has been persisted in database: {}", saved);
                        rowsToDto.add(csvEntityMapper.toDto(saved));
                    }
                }
            }
            return rowsToDto;
        } catch (IOException e) {
            log.debug("Exception: {} ", e.getMessage());
            throw new CsvFileException("Csv file can not parse to dto.");
        }
    }


    public ByteArrayInputStream fetchAllData() throws IOException {
        return generateCsvData(csvEntityMapper.toDto(csvRepository.findAll()));
    }


    // This method get csv dto and generate it to ByteArrayInputStream
    public ByteArrayInputStream generateCsvData(List<CsvEntityDto> allData) throws IOException {
        ByteArrayInOutStream stream = new ByteArrayInOutStream();
        OutputStreamWriter streamWriter = new OutputStreamWriter(stream);
        CSVWriter writer = new CSVWriter(
                streamWriter,
                CSVWriter.DEFAULT_SEPARATOR,
                CSVWriter.NO_QUOTE_CHARACTER,
                CSVWriter.NO_ESCAPE_CHARACTER,
                CSVWriter.DEFAULT_LINE_END);

        StatefulBeanToCsv<CsvEntityDto> beanToCsv = new StatefulBeanToCsvBuilder<CsvEntityDto>(writer)
                .withQuotechar(CSVWriter.NO_QUOTE_CHARACTER)
                .withSeparator(',')
                .build();

        try {
            beanToCsv.write(allData);
        } catch (CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
            throw new RuntimeException(e);
        }
        streamWriter.flush();
        return stream.getInputStream();
    }


}

