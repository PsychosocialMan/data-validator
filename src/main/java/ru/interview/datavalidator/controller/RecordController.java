package ru.interview.datavalidator.controller;

import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.interview.datavalidator.dao.RecordRepository;
import ru.interview.datavalidator.dao.entity.Record;
import ru.interview.datavalidator.dto.PageableRecordListDto;

import java.util.Date;

@RestController
@RequestMapping("/records")
public class RecordController {

    private final RecordRepository recordRepository;

    @Autowired
    public RecordController(RecordRepository recordRepository) {
        this.recordRepository = recordRepository;
    }

    @GetMapping(params = {"dateFrom", "dateTo"})
    ResponseEntity<PageableRecordListDto> getRecordsList(Pageable pageable,
                                               @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam("dateFrom") Date dateFrom,
                                               @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam("dateTo") Date dateTo) {
        val page = recordRepository.findAllByUpdatedTimestampBetween(dateFrom, dateTo, pageable);
        val pageableList = new PageableRecordListDto();
        pageableList.setPage(page.getNumber());
        pageableList.setSize(page.getSize());
        pageableList.setTotal(page.getNumberOfElements());
        pageableList.setData(page.toList());
        return ResponseEntity.ok(pageableList);
    }

    @GetMapping("/{id}")
    ResponseEntity<Record> getRecord(@PathVariable("id") String id) {
        return recordRepository.findById(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/admin/{id}")
    ResponseEntity deleteRecord(@PathVariable("id") String id) {
        recordRepository.deleteById(id);
        return ResponseEntity.accepted().build();
    }
}
