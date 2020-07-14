package ru.interview.datavalidator.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class PageableRecordListDto extends RecordListDto {

    private int page;

    private int size;
}
