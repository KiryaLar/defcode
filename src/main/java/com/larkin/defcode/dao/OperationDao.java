package com.larkin.defcode.dao;

import com.larkin.defcode.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class OperationDao {

    private final JdbcTemplate jdbcTemplate;

    public void checkOperationType(Integer operationTypeId) {
        log.debug("Check Operation Type: {}", operationTypeId);
        String sql = "SELECT operation_type FROM operation WHERE operation_type = ?";
        try{
            jdbcTemplate.queryForObject(sql, Integer.class, operationTypeId);
            log.debug("Operation Type is OK");
        } catch (EmptyResultDataAccessException e) {
            log.error("Operation Type is NOT OK");
            NotFoundException.operation(operationTypeId);
        }
    }
}
