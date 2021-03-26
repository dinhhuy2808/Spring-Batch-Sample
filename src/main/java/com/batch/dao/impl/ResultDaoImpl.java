package com.batch.dao.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.batch.constant.QuestionType;
import com.batch.dao.ResultDao;
import com.batch.model.Result;

/**
 * A DataAccessObject has the responsibility to do all SQL for us.
 */
@Repository("resultDaoImpl")
@Transactional(rollbackFor = Exception.class)
public class ResultDaoImpl implements ResultDao {
	
	@Autowired
	JdbcTemplate jdbcTemplate;

	private static final String DELETE_RESULT =
			"delete from result where hsk = ? and type = ?";
	
	private static final String DELETE_RESULT_TEST =
			"delete from result where hsk = ? and type = ? and test>10";

	private static final String INSERT_RESULT = 
			"INSERT INTO `result` " + 
			"(hsk, test, `number`, answer, `type`, part) " + 
			"VALUES(?, ?, ?, ?, ?, ?)";

	private static final String UPDATE_TESTS_PROMOTESETTING = "update promotesetting set tests = ? where hsk = ?";
	@Override
	public void delete(int hsk, QuestionType questionType) {
		if (questionType.equals(QuestionType.TEST)) {
			jdbcTemplate.update(DELETE_RESULT_TEST, hsk, questionType.name());
		} else {
			jdbcTemplate.update(DELETE_RESULT, hsk, questionType.name());
		}
		
	}

	@Override
	public void insertResult(List<Result> results) throws Exception {
		int[] insertCounts = jdbcTemplate.batchUpdate(INSERT_RESULT, new BatchPreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				Result result = results.get(i);
				ps.setInt(1, result.getHsk());
				ps.setInt(2, result.getTest());
				ps.setInt(3, result.getNumber());
				ps.setString(4, result.getAnswer());
				ps.setString(5, result.getType().name());
				ps.setString(6, result.getPart());
			}
			@Override
			public int getBatchSize() {
				return results.size();
			}
		});
		
		for (int insertCount: insertCounts) {
			if (insertCount == 0) {
				throw new Exception("Error While Inserting");
			}
		}
	}

	@Override
	public void updateTotalTests(int hsk, int totalTests) {
		jdbcTemplate.update(UPDATE_TESTS_PROMOTESETTING, totalTests, hsk);
	}
}