package org.gerimedica.exercise.service;


import org.gerimedica.exercise.domain.CsvEntity;
import org.gerimedica.exercise.repository.CsvRepository;
import org.gerimedica.exercise.service.dto.CsvEntityDto;
import org.gerimedica.exercise.service.mapper.CsvEntityMapper;
import org.gerimedica.exercise.service.mapper.CsvEntityMapperImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.file.Files;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@Import({CsvService.class, CsvEntityMapperImpl.class})
@DataJpaTest
@ActiveProfiles("test")
class CsvServiceTest {


    @Autowired CsvRepository csvRepository;
    @Autowired CsvService csvService;
    @Autowired
    CsvEntityMapper csvMapper;
    private final static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");

    @Test
    void testUploadCsvData_whenValidInput_thenReturn() throws Exception {
        // given
        File file = new File("src/test/resources/static/exercise.csv");
        MockMultipartFile multipartFile =
                new MockMultipartFile("tempCsvTest.tmp", "exercise.csv", "text/plain",
                        Files.newInputStream(file.toPath()));

        // when
        final List<CsvEntityDto> csvEntityDtos = csvService.uploadCsvFile(multipartFile);

        // then
        assertThat(csvEntityDtos).isNotNull().hasSize(18);

        List<CsvEntity> dataEntities = csvRepository.findAll();
        assertThat(dataEntities).isNotNull().hasSize(18);
    }

    @Test
    void fetchDataByCodeGetCsvFile_whenValidCode_successLoadDataTest() throws Exception {
        // given
        final CsvEntity csvEntity = buildDataEntityAndPersistThenGetFirst();
        List<CsvEntityDto> csvEntityDtos = csvMapper.toDto(List.of(csvEntity));
        ByteArrayInputStream data = csvService.generateCsvData(csvEntityDtos);

        // when
        final ByteArrayInputStream byteArrayInputStream = csvService.fetchByCodeGetCsvFile(csvEntity.getCode());

        //then
        assertThat(byteArrayInputStream).isNotNull();
        assertThat(new byte[byteArrayInputStream.available()]).isEqualTo(new byte[data.available()]);
    }

    @Test
    void fetchAllDataToCsvFile_successLoadAllDataTest() throws Exception {
        //given
        final CsvEntity csvEntityFirst = buildDataEntityAndPersistThenGetFirst();
        final CsvEntity csvEntitySecond = buildDataEntityAndPersistThenGetSecond();
        List<CsvEntityDto> csvEntityDtos = csvMapper.toDto(List.of(csvEntityFirst, csvEntitySecond));
        ByteArrayInputStream data = csvService.generateCsvData(csvEntityDtos);

        // when
        final ByteArrayInputStream byteArrayInputStream = csvService.fetchAllData();

        // then
        assertThat(byteArrayInputStream).isNotNull();
        assertThat(new byte[byteArrayInputStream.available()]).isEqualTo(new byte[data.available()]);
    }


    @Test
    void deleteAllData_successDeleteAllDataTest() throws Exception {
        //given
        final CsvEntity csvEntityFirst = buildDataEntityAndPersistThenGetFirst();
        final CsvEntity csvEntitySecond = buildDataEntityAndPersistThenGetSecond();

        //when
        csvService.deleteAll();

        //then
        final List<CsvEntity> dataEntities = csvRepository.findAll();
        assertThat(dataEntities).isNotNull().hasSize(0);
    }

    private CsvEntity buildDataEntityAndPersistThenGetFirst() throws ParseException {
        CsvEntity csvEntity = new CsvEntity();
        csvEntity.setSource("ZIB");
        csvEntity.setCode("307047009");
        csvEntity.setCodeListCode("ZIB003");
        csvEntity.setDisplayValue("Rectale temperatuur");
        csvEntity.setFromDate(simpleDateFormat.parse("01-01-2019"));
        csvEntity.setSortingPriority(1);
        return csvRepository.save(csvEntity);
    }

    private CsvEntity buildDataEntityAndPersistThenGetSecond() throws ParseException {
        CsvEntity csvEntity = new CsvEntity();
        csvEntity.setSource("ZIB");
        csvEntity.setCode("415945006");
        csvEntity.setCodeListCode("ZIB003");
        csvEntity.setDisplayValue("Rectale temperatuur");
        csvEntity.setFromDate(simpleDateFormat.parse("01-01-2019"));
        csvEntity.setSortingPriority(1);
        return csvRepository.save(csvEntity);
    }

}
