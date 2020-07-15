package com.ntscorp.gpa.googleSheets;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import com.google.common.reflect.TypeToken;
import com.ntscorp.gpa.exception.SheetDataFormatException;
import com.ntscorp.gpa.exception.SheetDataMappingException;
import com.ntscorp.gpa.googleSheets.connection.GoogleSheetsConnection;

public abstract class GoogleSheetsRepository<T extends GPAEntity> {

	@Autowired
	private GoogleSheetsConnection gpaGoogleSheetsConnection;
	@Autowired
	private ApplicationContext applicationContext;

	private final Logger logger = LoggerFactory.getLogger(GoogleSheetsRepository.class);

	private final Class<T> entityClass;
	private List<T> allList;
	private Map<String, Integer> columnMap;
	private final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

	public GoogleSheetsRepository() {
		// Generics의 type eraser를 보완하기 위해 런타임에도 클래스정보 저장
		TypeToken<T> typeToken = new TypeToken<>(getClass()) {
		};
		entityClass = (Class<T>) typeToken.getType();
	}

	@PostConstruct
	private void initBean() {
		if (entityClass.getSuperclass() != GPAEntity.class) {
			throw new SheetDataMappingException(entityClass + " 해당 엔티티가 GPAEntity를 상속해야 합니다.");
		}
		// DI 이후에 sheet 하나를 얻어와서 공유하도록 한다.
		List<List<Object>> sheet = gpaGoogleSheetsConnection.getSheet(entityClass.getSimpleName());
		updateColumnIndexMap(sheet);

		allList = new ArrayList<>();
		for (int rowNum = 1; rowNum < sheet.size(); rowNum++) {
			try {
				T instance = parseToEntity(sheet.get(rowNum), rowNum + 1);
				allList.add(instance);
			} catch (SheetDataFormatException | IndexOutOfBoundsException exception) {
				logger.warn("해당 row는 엔티티 클래스로 parse에 실패하였습니다" + sheet.get(rowNum) + "rowNum:" + rowNum);
			}
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
	}

	public List<T> getAll() {
		return allList;
	}

	public T getByRowNum(int rowNum) {
		return parseToEntity(
			gpaGoogleSheetsConnection.getSheet(entityClass.getSimpleName() + "!" + rowNum + ":" + rowNum).get(0), rowNum
		);
	}

	public void add(T data) {
		List<Object> row = new ArrayList<>(Collections.nCopies(columnMap.size(), ""));
		for (Field field : getAllFields()) {
			if (columnMap.get(field.getName()) == null) {
				continue;
			}
			Method getter = getGetter(field.getName());
			try {
				Object value = getter.invoke(data, new Object[]{});
				if (field.getType() == LocalDateTime.class) {
					value = ((LocalDateTime) getter.invoke(data, new Object[]{})).format(DATE_TIME_FORMAT);
				}
				row.set(columnMap.get(field.getName()), value != null ? value : "");
			} catch (IllegalAccessException | InvocationTargetException exception) {
				throw new SheetDataMappingException(entityClass + "Getter의 이름이나 파라미터가 맞지 않습니다.[" + field + "]", exception);
			}
		}

		int insertRowNum = gpaGoogleSheetsConnection.add(entityClass.getSimpleName(), row);
		data.setRowNum(insertRowNum);
		allList.add(data);
		for (Field field : getAllFields()) {
			LeftJoin leftJoin = field.getAnnotation(LeftJoin.class);
			if (leftJoin == null) {
				continue;
			}

			Method getter = getGetter(field.getName());
			try {
				List<GPAEntity> list = (List<GPAEntity>) getter.invoke(data, new Object[]{});
				GoogleSheetsRepository<GPAEntity> joinRepository =
					(GoogleSheetsRepository<GPAEntity>) applicationContext.getBean(getRepositoryName(leftJoin.targetClass()));
				for (GPAEntity joinElement : list) {
					Method setJoinColumnValue = getSetter(leftJoin.targetClass(), leftJoin.targetClass().getDeclaredField(leftJoin.joinColumn()));
					setJoinColumnValue.invoke(joinElement, insertRowNum);
					joinRepository.add(joinElement);
				}
			} catch (IllegalAccessException | InvocationTargetException | NoSuchFieldException exception) {
				throw new SheetDataMappingException("연관관계 매핑에 실패하였습니다.", exception);
			}
		}
	}

	public void update(T data) {
		allList.remove(data);
		List<Object> row = new ArrayList<>(Collections.nCopies(columnMap.size(), ""));
		for (Field field : getAllFields()) {
			if (columnMap.get(field.getName()) == null || field.getName().equals("rowNum")) {
				continue;
			}
			Method getter = getGetter(field.getName());
			try {
				Object value = getter.invoke(data, new Object[]{});
				if (field.getType() == LocalDateTime.class) {
					value = ((LocalDateTime) getter.invoke(data, new Object[]{})).format(DATE_TIME_FORMAT);
				}
				row.set(columnMap.get(field.getName()), value != null ? value : "");
			} catch (IllegalAccessException | InvocationTargetException exception) {
				throw new SheetDataMappingException(entityClass + "Getter의 이름이나 파라미터가 맞지 않습니다.[" + field + "]", exception);
			}
		}
		int rowNum = data.getRowNum();
		gpaGoogleSheetsConnection.update(entityClass.getSimpleName() + "!" + rowNum + ":" + rowNum, row);

		for (Field field : getAllFields()) {
			LeftJoin leftJoin = field.getAnnotation(LeftJoin.class);
			if (leftJoin == null) {
				continue;
			}

			Method getter = getGetter(field.getName());
			try {
				List<GPAEntity> list = (List<GPAEntity>) getter.invoke(data, new Object[]{});
				GoogleSheetsRepository<GPAEntity> joinRepository =
					(GoogleSheetsRepository<GPAEntity>) applicationContext.getBean(getRepositoryName(leftJoin.targetClass()));
				for (GPAEntity joinElement : list) {
					Method setJoinColumnValue = getSetter(leftJoin.targetClass(), leftJoin.targetClass().getDeclaredField(leftJoin.joinColumn()));
					setJoinColumnValue.invoke(joinElement, rowNum);
					if (joinElement.getRowNum() == 0) {
						joinRepository.add(joinElement);
					} else {
						joinRepository.update(joinElement);
					}
				}
			} catch (IllegalAccessException | InvocationTargetException | NoSuchFieldException exception) {
				throw new SheetDataMappingException("연관관계 매핑에 실패하였습니다.", exception);
			}
		}
		allList.add(data);
	}

	public void delete(T data) {
		int rowNum = data.getRowNum();
		gpaGoogleSheetsConnection.clear(entityClass.getSimpleName() + "!" + rowNum + ":" + rowNum);
		for (Field field : getAllFields()) {
			LeftJoin leftJoin = field.getAnnotation(LeftJoin.class);
			if (leftJoin == null) {
				continue;
			}

			Method getter = getGetter(field.getName());
			try {
				List<GPAEntity> list = (List<GPAEntity>) getter.invoke(data, new Object[]{});
				GoogleSheetsRepository<GPAEntity> joinRepository =
					(GoogleSheetsRepository<GPAEntity>) applicationContext.getBean(getRepositoryName(leftJoin.targetClass()));
				for (GPAEntity joinElement : list) {
					joinRepository.delete(joinElement);
				}
			} catch (IllegalAccessException | InvocationTargetException exception) {
				throw new SheetDataMappingException("연관관계 매핑에 실패하였습니다.", exception);
			}
		}
		allList.remove(data);
	}

	// TODO : parseToEntity에 rowNum이 있는게 맞나
	private T parseToEntity(List<Object> row, int rowNum) {

		T instance;
		try {
			instance = entityClass.getConstructor().newInstance();

		} catch (NoSuchMethodException | InstantiationException |
			IllegalAccessException | InvocationTargetException exception) {
			throw new SheetDataMappingException(entityClass + "생성자의 이름이나 파라미터가 맞지 않습니다. ", exception);
		}

		for (Field field : entityClass.getDeclaredFields()) {
			if (field.getAnnotation(LeftJoin.class) != null) {
				leftJoin(instance, field, rowNum);
			} else if (columnMap.get(field.getName()) != null && columnMap.get(field.getName()) < row.size()) {
				setField(instance, field, row.get(columnMap.get(field.getName())));
			}
		}

		instance.setRowNum(rowNum);
		return instance;
	}

	private void setField(T instance, Field field, Object value) {
		try {
			Method setter = this.getSetter(field);

			if (field.getType() == int.class) {
				setter.invoke(instance, Integer.parseInt(value.toString()));
			} else if (field.getType() == LocalDateTime.class) {
				setter.invoke(instance, LocalDateTime.parse(
					(String) value, DATE_TIME_FORMAT));
			} else if (field.getType() == String.class) {
				setter.invoke(instance, value);
			}
		} catch (IllegalAccessException | InvocationTargetException exception) {
			throw new SheetDataMappingException(entityClass + "Setter의 이름이나 파라미터가 맞지 않습니다.[" + field + "]", exception);
		} catch (NumberFormatException exception) {
			throw new SheetDataFormatException("Integer로 parse할 수 없습니다[" + field + "]", exception);
		} catch (DateTimeParseException exception) {
			throw new SheetDataFormatException(
				"[" + field + "]LocalDateTime으로 parse할 수 없습니다[format:" + DATE_TIME_FORMAT + "]", exception);
		}
	}

	private void leftJoin(T instance, Field field, int rowNum) {

		LeftJoin leftJoin = field.getAnnotation(LeftJoin.class);
		if (leftJoin == null || field.getType() != List.class) {
			return;
		}

		try {
			GoogleSheetsRepository<GPAEntity> joinRepository =
				(GoogleSheetsRepository<GPAEntity>) applicationContext.getBean(getRepositoryName(leftJoin.targetClass()));
			List<GPAEntity> list = joinRepository.getAll().stream()
				.filter(joinElement -> getJoinColumnNum(joinElement, leftJoin.joinColumn()) == rowNum)
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

	private int getJoinColumnNum(Object joinElement, String joinColumn) {
		try {
			Method getter = getGetter(joinElement.getClass(), joinColumn);
			return (int) getter.invoke(joinElement, new Object[]{});

		} catch (IllegalAccessException | InvocationTargetException exception) {
			throw new SheetDataMappingException(joinElement.getClass() + " Getter의 이름이나 파라미터가 맞지 않습니다. ", exception);
		}
	}

	private List<Field> getAllFields() {
		List<Field> fieldList = new ArrayList<>();
		for (Class<?> c = entityClass; c != null; c = c.getSuperclass()) {
			fieldList.addAll(Arrays.asList(c.getDeclaredFields()));
		}
		return fieldList;
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

	private Method getSetter(Class<?> targetClass, Field field) {
		try {
			String setterName = "set" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1);
			return targetClass.getMethod(setterName, field.getType());

		} catch (NoSuchMethodException exception) {
			throw new SheetDataMappingException(
				targetClass + "생성자 혹은 Setter의 이름이나 파라미터가 맞지 않습니다.[" + field + "]", exception);
		}
	}

	private Method getGetter(String fieldName) {
		try {
			String getterName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
			return entityClass.getMethod(getterName);
		} catch (NoSuchMethodException exception) {
			throw new SheetDataMappingException(entityClass + " Getter의 이름이나 파라미터가 맞지 않습니다. ", exception);
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
