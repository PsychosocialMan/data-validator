package ru.interview.datavalidator.dao.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Record {
    @Id
    @Column(name = "primary_key")
    @With
    String primaryKey;

    @With
    String name;

    @With
    String description;

    @Column(name = "updated_timestamp")
    @Temporal(TemporalType.TIMESTAMP)
    @With
    Date updatedTimestamp;
}
