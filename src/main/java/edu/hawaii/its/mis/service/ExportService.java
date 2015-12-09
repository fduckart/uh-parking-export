package edu.hawaii.its.mis.service;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
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
import org.springframework.integration.file.remote.session.Session;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import edu.hawaii.its.mis.sftp.SftpSessionFactory;

@Component
public class ParkingService {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private ResultRowMapper mapper = new ResultRowMapper();

    @Autowired
    private DataSource dataSource;

    @Autowired
    private SftpSessionFactory factory;

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

            createFile(results);
            transferFile();
        }

        logger.info("Finished file export.");
        logger.info("..................................................");
    }

    private void createFile(List<Result> results) throws IOException {
        logger.info("Writing output results to '{}'", filename);
        try (PrintWriter out = new PrintWriter(
                new BufferedWriter(
                        new FileWriter(filename)))) {
            for (Result r : results) {
                out.println(r.getValue());
            }
        }

        String outFileame = filename + "." + filenameTimestamp();
        logger.info("Creating output file copy '{}'", outFileame);
        Path filepath = Paths.get(filename);
        Files.copy(filepath, Paths.get(outFileame), REPLACE_EXISTING);
    }

    private void transferFile() {
        if (!factory.isEnabled()) {
            logger.info("SFTP is disabled. Use properties file to enable.");
            return;
        }

        Path source = Paths.get(filename);
        Path destination = Paths.get(filename);

        String filename = source.getFileName().toString();
        logger.info("SFTP start, putting " + filename);

        boolean copied = false;
        Session<LsEntry> session = null;
        ChannelSftp channelSftp = null;

        try {
            session = factory.getSession();
            Object obj = session.getClientInstance();
            if (obj instanceof ChannelSftp) {
                channelSftp = (ChannelSftp) obj;
                channelSftp.put(source.toString(), destination.toString());
                copied = true;
            }
        } catch (Exception ex) {
            logger.error("Error: ", ex);
        } finally {
            close(session);
            disconnect(channelSftp);
        }

        String msg = "SFTP '" + filename + "' copied "
                + "to destination '" + destination + "' ";
        msg += copied ? "successful. " : "failed. ";
        logger.info(msg);
    }

    private void close(Session<LsEntry> session) {
        try {
            if (session != null) {
                session.close();
            }
        } catch (Exception e) {
            logger.warn("Exception: " + e);
        }
    }

    private void disconnect(ChannelSftp channel) {
        try {
            if (channel != null && channel.isConnected()) {
                channel.exit();
            }
        } catch (Exception e) {
            logger.warn("Exception: " + e);
        }
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