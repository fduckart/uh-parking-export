package edu.hawaii.its.mis.service;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class ResultRowMapper implements RowMapper<Result> {

    public Result mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Result(rs.getString(1));
    }
}
