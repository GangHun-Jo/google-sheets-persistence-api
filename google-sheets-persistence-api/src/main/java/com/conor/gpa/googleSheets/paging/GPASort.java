package com.conor.gpa.googleSheets.paging;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;

import com.conor.gpa.exception.SheetDataMappingException;

public class GPASort {
	private String fieldName;
	private GPASortType type;

	public GPASort(String fieldName, GPASortType type) {
		this.fieldName = fieldName;
		this.type = type;

	}

	public <T> int compare(T a, T b) {
		if (!a.getClass().equals(b.getClass())) {
			return 0;
		}

		int result;
		Class<T> entityClass = (Class<T>) a.getClass();
		Method getter = getGetter(entityClass, fieldName);

		try {
			Object valueA = getter.invoke(a, new Object[]{});
			Object valueB = getter.invoke(b, new Object[]{});

			if (valueA.getClass() == int.class) {
				result = Integer.compare((int) valueA, (int) valueB);
			} else if (valueA.getClass() == String.class) {
				result = ((String) valueA).compareTo((String) valueB);
			} else if (valueA.getClass() == LocalDateTime.class) {
				result = ((LocalDateTime) valueA).compareTo((LocalDateTime) valueB);
			} else {
				result = 0;
			}
		} catch (IllegalAccessException | InvocationTargetException exception) {
			throw new RuntimeException(a + "와 " + b + "를  비교할 수 없습니다.", exception);
		}

		return type == GPASortType.ASC ? result : result * -1;
	}

	private Method getGetter(Class<?> targetClass, String fieldName) {
		try {
			String getterName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
			return targetClass.getMethod(getterName);
		} catch (NoSuchMethodException exception) {
			throw new SheetDataMappingException(fieldName + " Getter의 이름이나 파라미터가 맞지 않습니다. ", exception);
		}
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public GPASortType getType() {
		return type;
	}

	public void setType(GPASortType type) {
		this.type = type;
	}
}
