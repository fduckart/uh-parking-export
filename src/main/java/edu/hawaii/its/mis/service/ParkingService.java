package edu.hawaii.its.mis.service;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import edu.hawaii.its.mis.config.AppConfiguration;

@Component
public class ParkingService {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private ResultRowMapper mapper = new ResultRowMapper();

    @Autowired
    private AppConfiguration config;

    @Autowired
    private DataSource dataSource;

    @Value("${export.filename:parking_extract.txt}")
    private String filename;

    @Value("${export.timestamp.format}")
    private String filenameTimestamp;

    @Value("${export.sql}")
    private String sql;

    @PostConstruct
    public void afterPropertiesSet() {
        Assert.hasLength(sql, "property 'sql' is required");
        Assert.hasLength(filename, "property 'filename' is required");
    }

    public void export() throws Exception {
        logger.info("..................................................");
        logger.info("Start file export...");

        JdbcTemplate jdbcTemplate = jdbcTemplate();
        List<Result> results = jdbcTemplate.query(sql, mapper);
        logger.info("Results count: {}", results.size());
        if (results.size() > 0) {
            String outFileame = filename + "." + filenameTimestamp();
            logger.info("Writing output results to '{}'", filename);
            try (PrintWriter out = new PrintWriter(
                    new BufferedWriter(
                            new FileWriter(filename)))) {
                for (Result r : results) {
                    out.println(r.getValue());
                }
            }

            logger.info("Creating output file copy '{}'", outFileame);
            Files.copy(Paths.get(filename), Paths.get(outFileame), REPLACE_EXISTING);
        }

        logger.info("Finished file export.");
        logger.info("..................................................");
    }

    public JdbcTemplate jdbcTemplate() {
        return new JdbcTemplate(dataSource);
    }

    private String filenameTimestamp() {
        return formatDate(new Date(), filenameTimestamp);
    }

    private String formatDate(Date date, String formatStr) {
        String result;
        try {
            result = date.toString();
            SimpleDateFormat formatter = new SimpleDateFormat(formatStr);
            result = formatter.format(date);
        } catch (IllegalArgumentException e) {
            // Exception Ignored.
            result = "";
        }

        return result;
    }

}