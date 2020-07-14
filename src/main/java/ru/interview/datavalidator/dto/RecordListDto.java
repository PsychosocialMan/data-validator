package ru.interview.datavalidator.dto;

import lombok.Data;
import ru.interview.datavalidator.dao.entity.Record;

import java.util.List;

@Data
public class RecordListDto {

    private List<Record> data;

    private int total;
}
