package org.gerimedica.exercise.controller;

import org.gerimedica.exercise.domain.CsvEntity;
import org.gerimedica.exercise.repository.CsvRepository;
import org.gerimedica.exercise.service.CsvService;
import org.gerimedica.exercise.service.dto.CsvEntityDto;
import org.gerimedica.exercise.service.mapper.CsvEntityMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.context.WebApplicationContext;


import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.file.Files;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
class CsvControllerTest {

    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext webApplicationContext;
    private final static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");

    @Autowired
    CsvRepository csvRepository;

    @Autowired
    CsvService csvService;

    @Autowired
    CsvEntityMapper csvMapper;

    @BeforeEach
    public void setUp() {
        this.mockMvc = webAppContextSetup(webApplicationContext)
                .alwaysDo(print())
                .build();

        csvRepository.deleteAll();
    }


    @Test
    void testUploadCsvData_whenValidInput_thenReturn() throws Exception {
        // given
        File file = new File("src/test/resources/static/exercise.csv");
        MockMultipartFile multipartFile =
                new MockMultipartFile("file", "exercise.csv", "text/plain",
                        Files.newInputStream(file.toPath()));
        //
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/upload/")
                        .file(multipartFile))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.*", hasSize(18)));
    }

    @Test
    void testFetchDataByCode_whenValidCode_thenReturn() throws Exception {
        // given
        final CsvEntity csvEntity = buildDataEntityAndPersistThenGetFirst();

        List<CsvEntityDto> csvEntityDtos = csvMapper.toDto(List.of(csvEntity));
        ByteArrayInputStream data = csvService.generateCsvData(csvEntityDtos);
        byte[] array = new byte[data.available()];
        data.read(array);

        //
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/fetch/" + csvEntity.getCode() + "/")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().bytes(array));
    }


    @Test
    void testFetchAllData_whenValidCode_thenReturnAll() throws Exception {
        // given
        final CsvEntity csvEntityFirst = buildDataEntityAndPersistThenGetFirst();
        final CsvEntity csvEntitySecond = buildDataEntityAndPersistThenGetSecond();

        List<CsvEntityDto> csvEntityDtos = csvMapper.toDto(List.of(csvEntityFirst, csvEntitySecond));
        ByteArrayInputStream data = csvService.generateCsvData(csvEntityDtos);
        byte[] array = new byte[data.available()];
        data.read(array);

        //
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/fetch/all/")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().bytes(array));
    }


    @Test
    void testDeleteAllData_thenReturnSuccess() throws Exception {
        // given
        final CsvEntity csvEntityFirst = buildDataEntityAndPersistThenGetFirst();
        final CsvEntity csvEntitySecond = buildDataEntityAndPersistThenGetSecond();
        //
        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/api/v1/delete/all/")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
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
