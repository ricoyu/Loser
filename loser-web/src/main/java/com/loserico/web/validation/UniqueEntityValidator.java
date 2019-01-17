package com.loserico.web.validation;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.loserico.orm.dao.SQLOperations;
import com.loserico.web.validation.annotation.UniqueEntity;
import com.peacefish.spring.ApplicationContextHolder;

/**
 * If value of referenceField equals as specified in referenceValue, mandatoryField is
 * mandatory, otherwise not.
 * 
 * @author xuehyu
 * @since Aug 22, 2014
 *
 */
public class UniqueEntityValidator implements ConstraintValidator<UniqueEntity, Object> {

	private static final Logger logger = LoggerFactory.getLogger(UniqueEntityValidator.class);

	private SQLOperations sqlOperations;
	private String table = null;
	private String[] checkField = null;
	private String primaryKeyField = null;
	private String[] referenceField = null;
	private boolean isSoftDelete;

	@Override
	public void initialize(UniqueEntity constraintAnnotation) {
		table = constraintAnnotation.table();
		checkField = constraintAnnotation.fieldNames();
		primaryKeyField = constraintAnnotation.primaryKey();
		referenceField = constraintAnnotation.properties();
		isSoftDelete = constraintAnnotation.isSoftDelete();
	}

	@Override
	public boolean isValid(Object bean, ConstraintValidatorContext context) {
		Object primaryKey;
		try {
			primaryKey = BeanUtils.getProperty(bean, primaryKeyField);
			if (sqlOperations == null) {
				sqlOperations = ApplicationContextHolder.getBean(SQLOperations.class);
			}
			Object[] referencedFieldValues = new Object[referenceField.length];
			for (int i = 0; i < referenceField.length; i++) {
				referencedFieldValues[i] = BeanUtils.getProperty(bean, referenceField[i]);
			}

			StringBuilder sb = new StringBuilder();
			sb.append("SELECT COUNT(1) FROM ")
					.append(table)
					.append(" where 1=1 ");

			if (isSoftDelete) {
				sb.append(" and DELETED =0 ");
			}
			if (primaryKey != null) {
				sb.append(" and ID !=:primaryKey ");
			}
			
			//如果只有一个字段并且字段值为null，那么认为合法
			if(checkField.length == 1 && referencedFieldValues[0] == null) {
				return true;
			}
			
			for (int i = 0; i < checkField.length; i++) {
				if(referencedFieldValues[i] != null) {
					sb.append("and ").append(checkField[i]).append("=:" + referenceField[i] + " ");
				} else {
					sb.append("and ").append(checkField[i]).append(" is NULL ");
				}
			}
			Map<String, Object> params = new HashMap<>();
			if (primaryKey != null) {
				params.put("primaryKey", primaryKey);
			}
			for (int i = 0; i < referenceField.length; i++) {
				if(referencedFieldValues[i] != null) {
					params.put(referenceField[i], referencedFieldValues[i]);
				}
			}
			int count = sqlOperations.sqlCountQuery(sb.toString(), params);
			return count == 0;
		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			logger.error("", e);
		}

		return false;
	}

}
