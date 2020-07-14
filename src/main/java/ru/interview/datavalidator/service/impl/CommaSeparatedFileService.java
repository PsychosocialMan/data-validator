package ru.interview.datavalidator.service.impl;

import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.supercsv.cellprocessor.ParseDate;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.prefs.CsvPreference;
import ru.interview.datavalidator.dao.entity.Record;
import ru.interview.datavalidator.exception.FieldNotFoundException;
import ru.interview.datavalidator.exception.FileParsingException;
import ru.interview.datavalidator.exception.UnknownFieldException;
import ru.interview.datavalidator.service.FileService;
import sun.misc.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.*;

@Service
@Slf4j
@PropertySource("application.properties")
public class CommaSeparatedFileService implements FileService {

    private static final Map<String, String> PROPERTY_MAPPING = ImmutableMap.of(
            "PRIMARY_KEY", "primaryKey",
            "NAME", "name",
            "DESCRIPTION", "description",
            "UPDATED_TIMESTAMP", "updatedTimestamp"
    );

    @Value("${timestamp.pattern}")
    private String TIMESTAMP_PATTERN;


    public List<Record> process(InputStream bytes, Charset charset) {
        byte[] readBytes = new byte[0];
        try {
            // Read bytes from Input Stream with unknown length (-1).
            readBytes = IOUtils.readFully(bytes, -1, false);
        } catch (IOException e) {
            log.error("Unable to read bytes from Input Stream...");
            e.printStackTrace();
        }
        try {
            try (CsvBeanReader csvReader = new CsvBeanReader(new StringReader(new String(readBytes, charset)), CsvPreference.STANDARD_PREFERENCE)) {
                final String[] columnMapping = getColumnMapping(csvReader);
                final CellProcessor[] processors = getProcessors(columnMapping);

                val result = new ArrayList<Record>();
                Record record = new Record();
                do {
                    try {
                        record = csvReader.read(Record.class, columnMapping, processors);
                        if (record != null)
                            result.add(record);
                    } catch (IllegalArgumentException e) {
                        log.error("Error while parsing the record: {}", e.getMessage());
                        e.printStackTrace();
                    }
                } while (record != null);
                return result;
            }
        } catch (IOException e) {
            log.error("Error while parsing file: {}", e.getMessage());
            e.printStackTrace();
            throw new FileParsingException("IOException occurred while parsing CSV-file: " + e.getMessage());
        }
    }

    private CellProcessor[] getProcessors(String[] columnMapping) {
        final CellProcessor[] processors = new CellProcessor[PROPERTY_MAPPING.size()];

        ArrayList<CellProcessor> listProcessors = new ArrayList<>();
        for (val column : columnMapping) {
            if (column.equals("updatedTimestamp")) {
                listProcessors.add(new ParseDate(TIMESTAMP_PATTERN, false, Locale.ENGLISH));
            } else {
                listProcessors.add(new NotNull());
            }
        }
        return listProcessors.toArray(processors);
    }

    private String[] getColumnMapping(CsvBeanReader reader) {
        ArrayList<String> result = new ArrayList<>();
        String[] firstLine;
        try {
            firstLine = reader.getHeader(true);
        } catch (IOException e) {
            log.error("Unable to read headers from file...");
            e.printStackTrace();
            throw new FileParsingException("Unable to read headers from file...");
        }
        log.info("Found headers of file: {}", Arrays.toString(firstLine));
        for (val header : firstLine) {
            if (PROPERTY_MAPPING.containsKey(header))
                result.add(PROPERTY_MAPPING.get(header));
            else
                throw new UnknownFieldException("Unable to process column from file [" + header + "]");
        }
        if (result.size() < PROPERTY_MAPPING.size())
            throw new FieldNotFoundException("Unable to find required column...");
        String[] arrayResult = new String[result.size()];
        return result.toArray(arrayResult);
    }

    public List<Record> process(InputStream bytes) {
        return process(bytes, Charset.forName("UTF-8"));
    }
}
