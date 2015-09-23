package oing.webapp.android.sdkliteserver.dao;

import jodd.util.ReflectUtil;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * A class that simplify(a joke) statement executions.
 */
@Component
public abstract class BaseDao {
	@Autowired
	protected SqlSession sqlSession;

	// ---------- Operations: Select ----------

	/**
	 * Select on from database.
	 *
	 * @param statementName The {@code id} of {@code select} element in mybatis mapper file.
	 * @param parameter     A parameter object to pass to the statement.
	 */
	protected <T> T selectOne(String statementName, Object parameter) {
		return sqlSession.selectOne(getMapperNamespace() + "." + statementName, parameter);
	}

	/**
	 * Select bunch of records from database.
	 *
	 * @param statementName The {@code id} of {@code select} element in mybatis mapper file.
	 * @param parameter     A parameter object to pass to the statement.
	 */
	@SuppressWarnings("unchecked")
	protected <T> List<T> selectList(String statementName, Object parameter) {
		return (List<T>) sqlSession.selectList(getMapperNamespace() + "." + statementName, parameter);
	}

	// ---------- Operations: Insert ----------

	/**
	 * Insert a record to database by invoke "insert" statement form mapper file.
	 *
	 * @param model A parameter object to pass to the statement.
	 * @return Number of rows affected.
	 */
	protected int insert(Object model) {
		return sqlSession.insert(getMapperNamespace() + ".insert", model);
	}

	/**
	 * Insert a record to database by invoke your own statement from mapper file.
	 *
	 * @param statementName Your statement name from mapper file.
	 * @param parameter     A parameter object to pass the statement.
	 * @return Number of rows affected.
	 */
	protected int insert(String statementName, Object parameter) {
		return sqlSession.insert(getMapperNamespace() + "." + statementName, parameter);
	}

	/**
	 * Insert bunch of models into database by invoke "batchInsert" statement from mapper file.
	 *
	 * @param models The database parameter that will be insert to.
	 * @return Number of rows affected.
	 */
	protected <T> int insert(List<T> models) {
		return sqlSession.insert(getMapperNamespace() + ".batchInsert", models);
	}

	// ---------- Operations: Update ----------

	/**
	 * Update a record by its ID.
	 *
	 * @param parameter The parameter that will save to database.<br/>
	 *                  NOTE: The parameter needs a property called "id", which means update record by that id.
	 * @return Number of lines affected.
	 */
	protected int updateById(Object parameter) {
		return sqlSession.update(getMapperNamespace() + ".updateById", parameter);
	}

	/**
	 * Update a record with specific parameter by invoke your own statement from mapper file.
	 *
	 * @param statementName Your statement name from mapper file.
	 * @param parameter     The parameter that will go to work with your statement.
	 * @return Number of rows affected.
	 */
	protected int update(String statementName, Object parameter) {
		return sqlSession.update(getMapperNamespace() + "." + statementName, parameter);
	}

	/**
	 * If the {@code model} has a property called "id"(which means a getter method "getId" in it)
	 * and the data type of that property is Long,
	 * this method will update database record by that id by invoke {@link #updateById(Object)}.
	 * Otherwise insert it by invoke {@link #insert(Object)}.
	 *
	 * @param model The parameter which will insert or update to database.
	 * @return Number of rows affected.
	 */
	protected <T> int insertOrUpdateById(T model) {
		Object lObjId = null;
		try {
			lObjId = ReflectUtil.invokeDeclared(model, "getId");
		} catch (ReflectiveOperationException e) {
			// Ignore, cause method "getId" does not exist.
		}
		if (lObjId == null) {
			insert(model);
		}
		return updateById(model);
	}

	// ---------- Operations: Delete ----------

	/**
	 * Delete a record by invoke your own statement with parameter.
	 *
	 * @param statementName Your statement name from mapper file.
	 * @param parameter     The parameter that will go to work with your statement.
	 * @return Number of rows affected.
	 */
	protected int delete(String statementName, Object parameter) {
		return sqlSession.delete(getMapperNamespace() + "." + statementName, parameter);
	}

	/**
	 * The "namespace" from mybatis mapper xml file.
	 */
	protected abstract String getMapperNamespace();
}
