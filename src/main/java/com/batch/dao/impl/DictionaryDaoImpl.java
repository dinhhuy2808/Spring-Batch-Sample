package com.batch.dao.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.batch.dao.DictionaryDao;
import com.batch.model.Dictionary;

/**
 * A DataAccessObject has the responsibility to do all SQL for us.
 */
@Repository("dictionaryDaoImpl")
@Transactional(rollbackFor = Exception.class)
public class DictionaryDaoImpl implements DictionaryDao {

	@Autowired
	JdbcTemplate jdbcTemplate;

	private static final String DELETE_RESULT = "delete from dictionary;";

	private static final String ALTER_AUTO_INCREMENT = "ALTER TABLE dictionary AUTO_INCREMENT = 1";

	private static final String INSERT_RESULT = "INSERT INTO `dictionary` " + "(hantu, pinyin, nghia1, hanviet) "
			+ "VALUES(?, ?, ?, ?)";

	@Override
	public void deleteAll() {
		jdbcTemplate.update(DELETE_RESULT);
		jdbcTemplate.update(ALTER_AUTO_INCREMENT);
	}

	@Override
	public void insertAll(List<Dictionary> dictionaries) throws Exception {
		int[] insertCounts = jdbcTemplate.batchUpdate(INSERT_RESULT, new BatchPreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				Dictionary dictionary = dictionaries.get(i);
				ps.setString(1, dictionary.getHantu());
				ps.setString(2, dictionary.getPinyin());
				ps.setString(3, dictionary.getNghia1());
				ps.setString(4, dictionary.getHanviet());
			}

			@Override
			public int getBatchSize() {
				return dictionaries.size();
			}
		});

		for (int insertCount : insertCounts) {
			if (insertCount == 0) {
				throw new Exception("Error While Inserting");
			}
		}
	}
}