package com.ntscorp.gpa.googleSheets;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import com.google.common.reflect.TypeToken;
import com.ntscorp.gpa.exception.SheetDataFormatException;
import com.ntscorp.gpa.exception.SheetDataMappingException;
import com.ntscorp.gpa.googleSheets.connection.GoogleSheetsConnection;

public abstract class GoogleSheetsRepository<T> {

	@Autowired
	private GoogleSheetsConnection sefGoogleSheetsConnection;
	@Autowired
	private ApplicationContext applicationContext;

	private final Class<T> entityClass;
	private List<T> allList;
	private Map<String, Integer> columnMap;

	public GoogleSheetsRepository() {
		// Generics의 type eraser를 보완하기 위해 런타임에도 클래스정보 저장
		TypeToken<T> typeToken = new TypeToken<>(getClass()) {
		};
		entityClass = (Class<T>) typeToken.getType();
	}

	@PostConstruct
	private void initBean() {
		// DI 이후에 sheet 하나를 얻어와서 공유하도록 한다.
		List<List<Object>> sheet = sefGoogleSheetsConnection.getSheet(entityClass.getSimpleName());
		updateColumnIndexMap(sheet);

		allList = new ArrayList<>();
		for (int rowNum = 1; rowNum < sheet.size(); rowNum++) {
			allList.add(parseToEntity(sheet.get(rowNum)));
		}
	}

	private void updateColumnIndexMap(List<List<Object>> sheet) {
		columnMap = new HashMap<>();
		try {
			List<Object> columnNames = sheet.get(0);
			for (int colNum = 0; colNum < columnNames.size(); colNum++) {
				columnMap.put((String) columnNames.get(colNum), colNum);
			}
		} catch (IndexOutOfBoundsException exception) {
			throw new SheetDataMappingException("시트에 column 헤더가 존재하지 않습니다.", exception);
		}

		if (columnMap.get("id") == null) {
			throw new SheetDataMappingException("시트 column 헤더에 id가 존재하지 않습니다.");
		}
	}

	public List<T> getAll() {
		return allList;
	}

	private T parseToEntity(List<Object> row) {

		T instance;
		try {
			instance = entityClass.getConstructor().newInstance();

		} catch (NoSuchMethodException | InstantiationException |
			IllegalAccessException | InvocationTargetException exception) {
			throw new SheetDataMappingException(entityClass + "생성자의 이름이나 파라미터가 맞지 않습니다. ", exception);
		}

		for (Field field : entityClass.getDeclaredFields()) {
			if (field.getAnnotation(LeftJoin.class) != null) {
				leftJoin(instance, field, Integer.parseInt((String) row.get(columnMap.get("id"))));
			} else if (columnMap.get(field.getName()) != null && columnMap.get(field.getName()) < row.size()) {
				setField(instance, field, row.get(columnMap.get(field.getName())));
			}
		}
		return instance;


	}

	private void setField(T instance, Field field, Object value) {
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

		try {
			Method setter = this.getSetter(field);

			if (field.getType() == int.class) {
				setter.invoke(instance, Integer.parseInt((String) value));
			} else if (field.getType() == LocalDateTime.class) {
				setter.invoke(instance, LocalDateTime.parse(
					(String) value, dateTimeFormatter));
			} else if (field.getType() == String.class) {
				setter.invoke(instance, value);
			}
		} catch (IllegalAccessException | InvocationTargetException exception) {
			throw new SheetDataMappingException(entityClass + "Setter의 이름이나 파라미터가 맞지 않습니다.[" + field + "]", exception);
		} catch (NumberFormatException exception) {
			throw new SheetDataFormatException("Integer로 parse할 수 없습니다[" + field + "]", exception);
		} catch (DateTimeParseException exception) {
			throw new SheetDataFormatException(
				"[" + field + "]LocalDateTime으로 parse할 수 없습니다[format:" + dateTimeFormatter + "]", exception);
		}
	}

	private void leftJoin(T instance, Field field, int id) {

		LeftJoin leftJoin = field.getAnnotation(LeftJoin.class);
		if (leftJoin == null || field.getType() != List.class) {
			return;
		}

		try {
			GoogleSheetsRepository<?> joinRepository =
				(GoogleSheetsRepository<?>) applicationContext.getBean(getRepositoryName(leftJoin.targetClass()));
			List<?> list = joinRepository.getAll().stream()
				.filter(joinElement -> getJoinColumnId(joinElement, leftJoin.joinColumn()) == id)
				.collect(Collectors.toList());

			Method setter = getSetter(field);
			setter.invoke(instance, list);
		} catch (IllegalAccessException | InvocationTargetException exception) {
			throw new SheetDataMappingException(
				getSetter(field) + " 엔티티의 생성자 혹은 Setter의 이름이나 파라미터가 맞지 않습니다.", exception);
		} catch (NoSuchBeanDefinitionException exception) {
			throw new SheetDataMappingException("이름에 해당하는 bean이 존재하지 않습니다.", exception);

		}
	}

	private int getJoinColumnId(Object joinElement, String joinColumn) {
		try {
			Method getter = getGetter(joinElement.getClass(), joinColumn);
			return (int) getter.invoke(joinElement, new Object[]{});

		} catch (IllegalAccessException | InvocationTargetException exception) {
			throw new SheetDataMappingException(joinElement.getClass() + " Getter의 이름이나 파라미터가 맞지 않습니다. ", exception);
		}
	}

	private Method getSetter(Field field) {
		try {
			String setterName = "set" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1);
			return entityClass.getMethod(setterName, field.getType());

		} catch (NoSuchMethodException exception) {
			throw new SheetDataMappingException(
				entityClass + "생성자 혹은 Setter의 이름이나 파라미터가 맞지 않습니다.[" + field + "]", exception);
		}
	}

	private Method getGetter(Class<?> targetClass, String fieldName) {
		try {
			String getterName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
			return targetClass.getMethod(getterName);
		} catch (NoSuchMethodException exception) {
			throw new SheetDataMappingException(targetClass + " Getter의 이름이나 파라미터가 맞지 않습니다. ", exception);
		}
	}

	private String getRepositoryName(Class<?> targetEntity) {
		return targetEntity.getSimpleName().substring(0, 1).toLowerCase()
			+ targetEntity.getSimpleName().substring(1) + "Repository";
	}
}
