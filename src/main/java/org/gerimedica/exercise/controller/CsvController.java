package org.gerimedica.exercise.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gerimedica.exercise.exception.CsvFileException;
import org.gerimedica.exercise.service.CsvService;
import org.gerimedica.exercise.service.dto.CsvEntityDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class CsvController {

    private final CsvService csvService;

    @PostMapping(path = {"/upload"})
    public ResponseEntity<List<CsvEntityDto>> uploadFile(@RequestParam("file") MultipartFile file) {
        log.debug("REST request to upload csv file data");
        if (file.isEmpty()) {
            throw new CsvFileException("File is empty.");
        }
        List<CsvEntityDto> csvEntityDtos = csvService.uploadCsvFile(file);
        return ResponseEntity.ok(csvEntityDtos);
    }

    @GetMapping("/fetch/{code}")
    public ResponseEntity<byte[]> fetchDataByCode(@PathVariable("code") String code) {
        log.debug("REST request to get csv by `code` data");
        try {
            final ByteArrayInputStream data = csvService.fetchByCodeGetCsvFile(code);
            byte[] array = new byte[data.available()];
            data.read(array);

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(array);

        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }


    @GetMapping("/fetch/all")
    public ResponseEntity<byte[]> fetchAll() {
        log.debug("REST request to get csv all data");
        try {
            final ByteArrayInputStream data = csvService.fetchAllData();

            byte[] array = new byte[data.available()];
            data.read(array);

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(array);
        } catch (Exception e){
            return ResponseEntity.badRequest().build();
        }
    }


    // this part wasn't really clear for me! delete all and return a empty csv file? or delete all rows in file? or delete all database!
    @DeleteMapping("/delete/all")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteAllData() {
        log.debug("REST request to delete all data");
        csvService.deleteAll();
        return ResponseEntity.noContent().build();
    }

}
