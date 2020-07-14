package ru.interview.datavalidator.controller;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.interview.datavalidator.dao.RecordRepository;
import ru.interview.datavalidator.dao.entity.Record;
import ru.interview.datavalidator.dto.RecordListDto;
import ru.interview.datavalidator.exception.FileParsingException;
import ru.interview.datavalidator.service.impl.CommaSeparatedFileService;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

@RestController
@RequestMapping("/upload")
@Slf4j
public class FileController {

    private final CommaSeparatedFileService commaSeparatedFileService;

    private final RecordRepository recordRepository;

    @Autowired
    public FileController(CommaSeparatedFileService commaSeparatedFileService, RecordRepository recordRepository) {
        this.commaSeparatedFileService = commaSeparatedFileService;
        this.recordRepository = recordRepository;
    }

    @PostMapping
    public ResponseEntity<RecordListDto> handleFileUpload(HttpServletRequest request) {
        try {
            Charset charset = Charset.forName(request.getCharacterEncoding());
            log.info("Start parsing of input file...");
            List<Record> processedRecords = commaSeparatedFileService.process(request.getInputStream(), charset);
            log.info("Parsing successful. Parsed entities: {}", processedRecords);
            log.info("Start saving records into DB...");
            List<Record> savedRecords = Lists.newArrayList(recordRepository.saveAll(processedRecords));
            log.info("Saved in DB successful. Saved entities: {}", savedRecords);
            val savedRecordsDto = new RecordListDto();
            savedRecordsDto.setTotal(savedRecords.size());
            savedRecordsDto.setData(savedRecords);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedRecordsDto);
        } catch (IOException e) {
            log.error("Error while processing binary file upload. Unable to get file from request...");
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        } catch (FileParsingException e){
            log.error("Error while parsing incoming file...");
            e.printStackTrace();
            return ResponseEntity.unprocessableEntity().build();
        }

    }

}
