package com.batch.dao.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLType;
import java.sql.Types;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.batch.dao.DictionaryDao;
import com.batch.dao.LessonDictionaryDao;
import com.batch.model.Dictionary;
import com.batch.model.LessonDictionary;

/**
 * A DataAccessObject has the responsibility to do all SQL for us.
 */
@Repository("lessonDictionaryDaoImpl")
@Transactional(rollbackFor = Exception.class)
public class LessonDictionaryDaoImpl implements LessonDictionaryDao {

	@Autowired
	JdbcTemplate jdbcTemplate;

	private static final String DELETE_RESULT = "delete from lessondictionary;";

	private static final String ALTER_AUTO_INCREMENT = "ALTER TABLE lessondictionary AUTO_INCREMENT = 1";

	private static final String INSERT_RESULT = "INSERT INTO elearning.lessondictionary\r\n" + 
			"(hantu, pinyin, nghia1, hsk, lesson, part, standart, popular, refid, `order`) " + 
			"VALUES(?, ?, ?, ?, ?, ?, ?, ?, NULL, ?); ";
	
	private static final String UPDATE_WORD_WITH_LESSON = " update lessondictionary set refid = (select id from dictionary where hantu = ? LIMIT 1)"
			+ " where hantu = ? ";

	@Override
	public void deleteAll() {
		jdbcTemplate.update(DELETE_RESULT);
		jdbcTemplate.update(ALTER_AUTO_INCREMENT);
	}

	@Override
	public void insertAll(List<LessonDictionary> dictionaries) throws Exception {
		int[] insertCounts = jdbcTemplate.batchUpdate(INSERT_RESULT, new BatchPreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				LessonDictionary dictionary = dictionaries.get(i);
				ps.setString(1, dictionary.getHantu());
				ps.setString(2, dictionary.getPinyin());
				ps.setString(3, dictionary.getNghia1());
				ps.setInt(4, dictionary.getHsk());
				if (dictionary.getLesson() == null) {
					ps.setNull(5, Types.VARCHAR);
				} else {
					ps.setString(5, dictionary.getLesson());
				}
				if (dictionary.getPart() == null) {
					ps.setNull(6, Types.VARCHAR);
				} else {
					ps.setString(6, dictionary.getPart());
				}
				if (dictionary.getStandart() == null) {
					ps.setNull(7, Types.INTEGER);
				} else {
					ps.setInt(7, dictionary.getStandart());
				}
				if (dictionary.getPopular() == null) {
					ps.setNull(8, Types.INTEGER);
				} else {
					ps.setInt(8, dictionary.getPopular());
				}
				if (dictionary.getOrder() == null) {
					ps.setNull(9, Types.INTEGER);
				} else {
					ps.setInt(9, dictionary.getOrder());
				}
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

	@Override
	public void updateWordWithLesson(List<LessonDictionary> dictionaries) throws Exception {
		int[] insertCounts = jdbcTemplate.batchUpdate(UPDATE_WORD_WITH_LESSON, new BatchPreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				LessonDictionary dictionary = dictionaries.get(i);
				ps.setString(1, dictionary.getHantu());
				ps.setString(2, dictionary.getHantu());
			}

			@Override
			public int getBatchSize() {
				return dictionaries.size();
			}
		});
	}
}