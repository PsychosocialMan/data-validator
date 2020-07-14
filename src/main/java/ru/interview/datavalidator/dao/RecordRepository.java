package ru.interview.datavalidator.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import ru.interview.datavalidator.dao.entity.Record;

import java.util.Date;

public interface RecordRepository extends CrudRepository<Record, String> {

    Page<Record> findAllByUpdatedTimestampBetween(@Param("after") Date after, @Param("before") Date before, Pageable page);
}
