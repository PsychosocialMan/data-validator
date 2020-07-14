package ru.interview.datavalidator.service;

import org.springframework.stereotype.Service;
import ru.interview.datavalidator.dao.entity.Record;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;

public interface FileService {

    List<Record> process(InputStream bytes, Charset charset);

    List<Record> process(InputStream bytes);
}
