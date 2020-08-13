package com.ntscorp.gpa.googleSheets;

import java.util.List;
import java.util.function.Predicate;

import com.ntscorp.gpa.googleSheets.entity.GPAEntity;

public interface SheetsRepository<T extends GPAEntity> {
	List<T> getAll();

	T getByRowNum(int rowNum);

	List<T> selectWhere(Predicate<T> condition);

	T selectOneWhere(Predicate<T> condition);

	void add(T data);

	void update(T data);

	void delete(T data);

}
